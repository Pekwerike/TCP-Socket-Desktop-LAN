package FileTransferProtocol;

import java.io.*;
import java.net.Socket;

public class FileTransferProtocol {
    private Socket mSocket;

    public FileTransferProtocol(Socket socket) {
        this.mSocket = socket;
    }

    public void optimizedReceiveFile() throws IOException {
        InputStream socketIS = mSocket.getInputStream();
        BufferedInputStream socketBIS = new BufferedInputStream(socketIS);
        DataInputStream socketDIS = new DataInputStream(socketBIS);

        while (true) {
            // get the number of files received
            int filesCount = socketDIS.readInt();

            if(filesCount > 0) {
                String[] filesName = new String[filesCount];
                int[] filesLength = new int[filesCount];

                // read out the length and name of each file received
                for (int i = 0; i < filesCount; i++) {
                    try {
                        filesName[i] = socketDIS.readUTF();
                    }catch (UTFDataFormatException malformedInput){
                        filesName[i] = String.format("%d", System.currentTimeMillis());
                    }
                    filesLength[i] = (int) socketDIS.readLong();
                }

                // read out the bytes of each file received
                for (int i = 0; i < filesCount; i++) {
                    String fileName = filesName[i];
                    long fileLength = filesLength[i];
                    if (fileName.startsWith("Directory") && fileLength == 0) {
                        fileName = fileName.substring(8);
                        for (int j = i + 1; j < filesCount; j++) {
                            if (filesName[j].startsWith("Directory") && filesLength[j] == 0) {
                                // reached a new directory, so go back
                                break;
                            }
                            // file is a directory, fetch all files and save them into the directory
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
                    }
                }
               break;
            }
        }
    }

    public void optimizedTransferFile(File[] fileCollection) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        int temLength = fileCollection.length;
        int filesCount = temLength;
        for(int i = 0; i < temLength; i++){
            if(fileCollection[i].isDirectory()){
                filesCount += fileCollection[i].listFiles().length;
            }
        }
        // write the number of files sent
        socketDOS.writeInt(filesCount);

        // write the name and length of the files to transfer
        for (int i = 0; i < fileCollection.length; i++) {
            if (fileCollection[i].isDirectory()) {
                //write the name of the directory to the socketDOS
                socketDOS.writeUTF("Directory" + fileCollection[i].getName());
                socketDOS.writeLong(0);
                File[] filesInFolder = fileCollection[i].listFiles();
                // write the name and length of all files in this folder
                for (int j = 0; j < filesInFolder.length; j++) {
                    socketDOS.writeLong(filesInFolder[j].length());
                    socketDOS.writeUTF(filesInFolder[j].getName());
                }
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
            }
        }

    }

    public void transferFile(File[] fileCollection) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        // write the number of files to transfer to the socketDOS
        socketDOS.writeInt(fileCollection.length);

        // write the size and name of each file to the socketDOS
        for (int i = 0; i < fileCollection.length; i++) {
            socketDOS.writeLong(fileCollection[i].length());
            socketDOS.writeUTF(fileCollection[i].getName());
        }

        // write the bytes of each file to the socketDOS
        for (int i = 0; i < fileCollection.length; i++) {
            FileInputStream fileIS = new FileInputStream(fileCollection[i]);
            byte[] buffer = fileIS.readAllBytes();
            socketDOS.write(buffer);
            fileIS.close();
        }
    }

    public void receiveFile() throws IOException {
        InputStream socketIS = mSocket.getInputStream();
        BufferedInputStream socketBIS = new BufferedInputStream(socketIS);
        DataInputStream socketDIS = new DataInputStream(socketBIS);
        while (true) {
            // read the number of files sent
            int filesCount = socketDIS.readInt();
            int[] filesLength = new int[filesCount];
            String[] filesName = new String[filesCount];

            if (filesCount > 0) {
                // read the size and name of each file sent
                for (int i = 0; i < filesCount; i++) {
                    filesLength[i] = (int) socketDIS.readLong();
                    filesName[i] = socketDIS.readUTF();
                }

                // read out the bytes for each file sent
                for (int i = 0; i < filesCount; i++) {
                    FileOutputStream fileOS = new FileOutputStream(createFile(filesName[i]));
                    int bytesUnread = filesLength[i];
                    byte[] buffer = new byte[1_000_000];

                    while (bytesUnread > 0) {
                        int bytesRead = socketDIS.read(buffer, 0, Math.min(bytesUnread, buffer.length));
                        if (bytesRead == -1) {
                            //End of file reached
                            break;
                        }
                        fileOS.write(buffer, 0, bytesRead);
                        bytesUnread = bytesUnread - bytesRead;
                    }
                }
                break;
            }

        }
    }

    private static File createFile(String fileName) {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\" + fileName);
    }

    private static File saveFileInFolder(String folderName, String fileName) {
        File folder = new File("C:\\Users\\Prosper's PC\\Pictures\\" + folderName);
        if (!folder.isDirectory()) folder.mkdirs();
        return new File(folder, fileName);
    }
}
