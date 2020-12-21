package FileTransferProtocol;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A generalized file transfer protocol, to transfer and receive multiple files and folders in a single socket stream
 * Algorithm created by P.C. Ekwerike
 **/

public class OptimizedFileTransferProtocol {
    private Socket mSocket;

    public OptimizedFileTransferProtocol(Socket socket) {
        this.mSocket = socket;
    }

    public void optimizedReceiveFile() throws IOException {
        InputStream socketIS = mSocket.getInputStream();
        BufferedInputStream socketBIS = new BufferedInputStream(socketIS);
        DataInputStream socketDIS = new DataInputStream(socketBIS);

            // get the number of files received
            int filesCount = socketDIS.readInt();

            if (filesCount > 0) {
                String[] filesName = new String[filesCount];
                int[] filesLength = new int[filesCount];
                ArrayList<Integer> filesInFolderCount = new ArrayList<>();

                // read out the length and name of each file received
                for (int i = 0; i < filesCount; i++) {
                    filesLength[i] = (int) socketDIS.readLong();
                    filesName[i] = socketDIS.readUTF();

                    if (filesName[i].startsWith("Directory")) {
                        /**if file is a directory, read out the number (int) of files in the directory from the
                         * socketDIS**/
                        filesInFolderCount.add(socketDIS.readInt());
                    }
                }

                // read out the bytes of each file received
                for (int i = 0; i < filesCount; i++) {
                    String fileName = filesName[i];
                    int fileLength = filesLength[i];
                    if (fileName.startsWith("Directory") && fileLength == 0) {
                        fileName = fileName.substring(9); // remove the string "Directory" from the folder name
                        /**Since file is a directory, use the number of files in the directory to set the loop limit
                         * for reading out files from the socketDIS, that will be saved under this directory**/
                        int folderFileCount = filesInFolderCount.get(0);
                        filesInFolderCount.remove(0);
                        int newCount = i + folderFileCount + 1;
                        for (int j = i + 1; j < newCount; j++) {
                            if (filesName[j].startsWith("Directory") && filesLength[j] == 0) {
                                // reached a new directory, so go back
                                break;
                            }
                            FileOutputStream fileOS = new FileOutputStream(saveFileInFolder(fileName, filesName[j]));
                            int unreadBytes = filesLength[j];
                            byte[] buffer;
                            try {
                                buffer = new byte[filesLength[j]];
                            } catch (OutOfMemoryError outOfMemoryError) {
                                buffer = new byte[1_000_000];
                            }
                            while (unreadBytes > 0) {
                                int readBytes = socketDIS.read(buffer, 0, Math.min(unreadBytes, buffer.length));
                                if (readBytes == -1) {
                                    //End of file reached
                                    break;
                                }
                                fileOS.write(buffer, 0, readBytes);
                                unreadBytes -= readBytes;
                            }
                            fileOS.close();
                            // move i to the next index of the filesCount
                            i = j;
                        }
                    } else {
                        // not directory, save file in base directory
                        FileOutputStream fileOS = new FileOutputStream(createFile(fileName));
                        int unreadBytes = fileLength;
                        byte[] buffer;
                        try {
                            buffer = new byte[fileLength];
                        } catch (OutOfMemoryError outOfMemoryError) {
                            buffer = new byte[1_000_000];
                        }
                        while (unreadBytes > 0) {
                            int readBytes = socketDIS.read(buffer, 0, Math.min(unreadBytes, buffer.length));
                            if (readBytes == -1) {
                                //End of file reached
                                break;
                            }
                            fileOS.write(buffer, 0, readBytes);
                            unreadBytes -= readBytes;
                        }
                        fileOS.close();
                    }
                }

            }

    }

    public void optimizedTransferFile(File[] fileCollection) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        int temLength = fileCollection.length;
        int filesCount = temLength;
        for (int i = 0; i < temLength; i++) {
            if (fileCollection[i].isDirectory()) {
                filesCount += fileCollection[i].listFiles().length;
            }
        }
        // write the number of files sent
        socketDOS.writeInt(filesCount);

        // write the name and length of the files to transfer
        for (int i = 0; i < fileCollection.length; i++) {
            if (fileCollection[i].isDirectory()) {
                //write the name of the directory to the socketDOS
                socketDOS.writeLong(0L);
                socketDOS.writeUTF("Directory" + fileCollection[i].getName());

                File[] filesInFolder = fileCollection[i].listFiles();
                socketDOS.writeInt(filesInFolder.length); // write the number of files in the directory to the socketDOS
                // write the name and length of all files in this directory
                for (int j = 0; j < filesInFolder.length; j++) {
                    socketDOS.writeLong(filesInFolder[j].length());
                    socketDOS.writeUTF(filesInFolder[j].getName());
                }
            } else {
                // not directory
                socketDOS.writeLong(fileCollection[i].length());
                socketDOS.writeUTF(fileCollection[i].getName());
            }
        }

        // write the bytes of the files to transfer
        for (int i = 0; i < fileCollection.length; i++) {
            if (fileCollection[i].isDirectory()) {
                // write the bytes of each file in the directory to the socketDOS
                File[] filesInFolder = fileCollection[i].listFiles();
                for (int j = 0; j < filesInFolder.length; j++) {
                    FileInputStream fileIS = new FileInputStream(filesInFolder[j]);
                    byte[] buffer = fileIS.readAllBytes();
                    socketDOS.write(buffer);
                    fileIS.close();
                }
            } else {
                // not directory
                FileInputStream fileIS = new FileInputStream(fileCollection[i]);
                byte[] buffer = fileIS.readAllBytes();
                socketDOS.write(buffer);
                fileIS.close();
            }
        }

    }

    private static File saveFileInFolder(String folderName, String fileName) throws IOException {
        File folder = new File("C:\\Users\\Prosper's PC\\Pictures\\" + folderName);
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, fileName);
    }

    private static File createFile(String fileName) {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\" + fileName);
    }

}
