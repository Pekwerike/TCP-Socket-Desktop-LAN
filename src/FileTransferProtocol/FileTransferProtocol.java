package FileTransferProtocol;

import java.io.*;
import java.net.Socket;

public class FileTransferProtocol {
    private Socket mSocket;

    public FileTransferProtocol(Socket socket) {
        this.mSocket = socket;
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

        }
    }
}
