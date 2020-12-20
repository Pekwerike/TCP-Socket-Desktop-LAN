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

    public void receiveFiles() throws IOException{
        InputStream socketIS = mSocket.getInputStream();
        BufferedInputStream socketBIS = new BufferedInputStream(socketIS);
        DataInputStream socketDIS = new DataInputStream(socketBIS);

        // read the number of files received
        int fileCount = socketDIS.readInt();
        int[] filesLength = new int[fileCount];
        String[] filesName = new String[fileCount];
        ArrayList<Integer> filesCountInFolder = new ArrayList<>();

        // read the name and length of the files received
        for(int i = 0; i < fileCount; i++){
            filesLength[i] = (int) socketDIS.readLong();
            filesName[i] = socketDIS.readUTF();
            if(filesName[i].startsWith("Directory")){
                filesCountInFolder.add(socketDIS.readInt());
            }
        }


    }
    public void transferFiles(File folder) throws IOException {
        OutputStream socketOS = mSocket.getOutputStream();
        BufferedOutputStream socketBOS = new BufferedOutputStream(socketOS);
        DataOutputStream socketDOS = new DataOutputStream(socketBOS);

        int filesCount = getFilesCount(folder);
        // write the total number of files to transfer to the socketDOS
        socketDOS.writeInt(filesCount);

        // write the name and length of each file inside the folder to the socketDOS
        ArrayList<File> allFilesInFolder = straightenFiles(folder);
        for(int i = 0; i < allFilesInFolder.size(); i++){
            File currentFile = allFilesInFolder.get(i);
            if(currentFile.isDirectory()){
                socketDOS.writeLong(0l);
                socketDOS.writeUTF("Directory"+ currentFile.getName());
                socketDOS.writeInt(currentFile.listFiles().length); // write the number of files in this directory to the socketDOS
            }else {
                socketDOS.writeLong(currentFile.length());
                socketDOS.writeUTF(currentFile.getName());
            }
        }

        // write the bytes of each file inside the folder to the socketDOS
        for(int i = 0; i < allFilesInFolder.size(); i++){
            File currentFile = allFilesInFolder.get(i);
            if(currentFile.isDirectory()){
                // move to the files under this directory
                continue;
            }else{
                FileInputStream fileIS = new FileInputStream(currentFile);
                byte[] buffer = fileIS.readAllBytes();
                socketDOS.write(buffer);
                fileIS.close();
            }
        }

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
