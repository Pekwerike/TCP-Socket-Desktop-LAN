package FileTransferProtocol;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * This class makes use of the concept of recursion to transfer multiple files nested in several directories using
 * a single socket stream
 **/
public class GeneralizedFileTransferProtocol {
    private Socket mSocket;

    public GeneralizedFileTransferProtocol(Socket socket) {
        this.mSocket = socket;
    }

    public void receiveFiles() throws IOException {
        InputStream socketIS = mSocket.getInputStream();
        BufferedInputStream socketBIS = new BufferedInputStream(socketIS);
        DataInputStream socketDIS = new DataInputStream(socketBIS);

        // read the number of files received
        int fileCount = socketDIS.readInt();

        // initialize import collection variables with the filesCount read
        int[] filesLength = new int[fileCount];
        String[] filesName = new String[fileCount];

        ArrayList<String> filesNameAL = new ArrayList<>();
        ArrayList<Integer> filesLengthAL = new ArrayList<>();
        ArrayList<Integer> filesCountInFolder = new ArrayList<>();
        HashMap<String, Integer> directoryCount = new HashMap<>();

        // read the name and length of the files received
        for (int i = 0; i < fileCount; i++) {
            filesLength[i] = (int) socketDIS.readLong();
            filesLengthAL.add(filesLength[i]);
            filesName[i] = socketDIS.readUTF();
            filesNameAL.add(filesName[i]);
            if (filesName[i].startsWith("Directory")) {
                directoryCount.put(filesName[i].substring(9), socketDIS.readInt());
               // filesCountInFolder.add(socketDIS.readInt());
            }
        }

        while(true) {
            String firstDirectoryName = filesNameAL.get(0).substring(9);
            // read and save the bytes of each file received
            readAndSaveFilesRecursively(1,firstDirectoryName , socketDIS,
                    filesNameAL, filesLengthAL, directoryCount,firstDirectoryName);
            break;
        }
    }

    private void readAndSaveFilesRecursively(
            int readIndex, String directoryName,
            DataInputStream socketDIS,
            ArrayList<String> filesName,
            ArrayList<Integer> filesLength,
            //ArrayList<Integer> directoryFilesCount,
            HashMap<String, Integer> directoryFilesCount,
            String directoryPlainName
    ) throws IOException {
        int currentDirectoryFilesCount = directoryFilesCount.get(directoryPlainName);
        /*int currentDirectoryFilesCount = directoryFilesCount.get(0);
        directoryFilesCount.remove(0);*/

        for (int i = readIndex; i < readIndex + currentDirectoryFilesCount; i++) {
            if (filesName.get(i).startsWith("Directory") && filesLength.get(i) == 0) {
                // a new directory has been reached inside this directory
                String nextDirectoryPlainName = filesName.get(i).substring(9);
                String nextDirectoryName = directoryName + "\\" + nextDirectoryPlainName;
                int nextReadIndex = i + 1;

                int nextDirectoryFilesCount = directoryFilesCount.get(filesName.get(i).substring(9));

                readAndSaveFilesRecursively(nextReadIndex, nextDirectoryName, socketDIS, filesName, filesLength, directoryFilesCount,
                        nextDirectoryPlainName);
                i += nextDirectoryFilesCount;
                continue;
            }
            FileOutputStream fileOS;
            try {
                fileOS = new FileOutputStream(FileTransferUtils.saveFileInFolder(directoryName, filesName.get(i)));
            } catch (FileNotFoundException fileNotFoundException) {
                continue;
            }
            byte[] buffer;
            try {
                buffer = new byte[filesLength.get(i)];
            } catch (OutOfMemoryError outOfMemoryError) {
                buffer = new byte[1_000_000];
            }
            int unreadBytes = filesLength.get(i);
            int readBytes;
            while (unreadBytes > 0) {
                readBytes = socketDIS.read(buffer, 0, Math.min(unreadBytes, buffer.length));
                if (readBytes == -1) {
                    break; // end of stream
                }
                fileOS.write(buffer, 0, readBytes);
                unreadBytes -= readBytes;
            }
            fileOS.close();
        }
    }



    public void transferFiles(File folder) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        int filesCount = FileTransferUtils.getFilesCount(folder);
        Hashtable<String, Integer> directoryFilesCount = FileTransferUtils.directoryFilesCount(folder);
        // write the total number of files to transfer to the socketDOS
        socketDOS.writeInt(filesCount);

        // write the name and length of each file inside the folder to the socketDOS
        ArrayList<File> allFilesInFolder = FileTransferUtils.straightenFolderIntoList(folder);
        for (File currentFile : allFilesInFolder) {
            if (currentFile.isDirectory()) {
                socketDOS.writeLong(0l);
                socketDOS.writeUTF("Directory" + currentFile.getName());
                socketDOS.writeInt(directoryFilesCount.get(currentFile.getName())); // write the number of files in this directory to the socketDOS
            } else {
                socketDOS.writeLong(currentFile.length());
                socketDOS.writeUTF(currentFile.getName());
            }
        }

        while(true) {
            boolean terminate = false;
            // write the bytes of each file inside the folder to the socketDOS
            for (File currentFile : allFilesInFolder) {
                if (currentFile.isDirectory()) {
                    // move to the files under this directory
                } else {
                    FileInputStream fileIS = new FileInputStream(currentFile);
                    byte[] buffer = fileIS.readAllBytes();
                    try {
                        socketDOS.write(buffer);
                    } catch (SocketException connectionResetByPeer) {
                        terminate = true;
                        break;
                    }
                    fileIS.close();
                }
            }
            if(terminate) break;
        }

    }
}
