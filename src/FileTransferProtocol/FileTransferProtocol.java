package FileTransferProtocol;

import java.io.*;
import java.net.Socket;

public class FileTransferProtocol {
    private Socket mSocket;

    public FileTransferProtocol(Socket socket) {
        this.mSocket = socket;
    }

    public void optimizedTransferFile(File[] fileCollection) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        for(int i = 0; i < fileCollection.length; i++){
            if(fileCollection[i].isDirectory()){

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
}
