package FileTransferProtocol;

import java.io.*;
import java.net.Socket;

/**
 * This class makes use of the concept of recursion to transfer multiple files nested in several directories using
 * a single socket stream
 **/
public class RecursiveFileTransferProtocol {
    private Socket mSocket;

    public RecursiveFileTransferProtocol(Socket socket) {
        this.mSocket = socket;
    }

    public void transferFiles(File[] fileCollection) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        int filesCount = 0;

        for (int i = 0; i < fileCollection.length; i++) {
            if (fileCollection[i].isDirectory()) {
                filesCount += fileCollection[i].listFiles().length;
            } else {
                filesCount += 1;
            }
        }
        // write the total number of files to transfer
        socketDOS.writeInt(filesCount);
    }

    private int getFilesCount(File folder){
        int filesCount = 1;
        File[] filesInFolder = folder.listFiles();

        for(int i = 0; i < filesInFolder.length; i++){
            if(filesInFolder[i].isDirectory()){
                filesCount += getFilesCount(filesInFolder[i]);
            }else {
                filesCount += 1;
            }
        }
        return filesCount;
    }
}
