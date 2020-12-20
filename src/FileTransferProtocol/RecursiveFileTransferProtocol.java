package FileTransferProtocol;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class makes use of the concept of recursion to transfer multiple files nested in several directories using
 * a single socket stream
 **/
public class RecursiveFileTransferProtocol {
    private Socket mSocket;

    public RecursiveFileTransferProtocol(Socket socket) {
        this.mSocket = socket;
    }

    public void transferFiles(File folder) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        int filesCount = getFilesCount(folder);
        // write the total number of files to transfer
        socketDOS.writeInt(filesCount);

        // write the name and length of each file inside the folder

    }


    private static ArrayList<File> straightenFiles(File folder){
        ArrayList<File> filesToReturn = new ArrayList<>();
        File[] filesInThisFolder = folder.listFiles();
        filesToReturn.add(folder);

        for(int i = 0; i < filesInThisFolder.length; i++){
            if(filesInThisFolder[i].isDirectory()){
                filesToReturn.addAll(straightenFiles(filesInThisFolder[i]));
            }else {
                filesToReturn.add(filesInThisFolder[i]);
            }
        }
        return filesToReturn;
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
