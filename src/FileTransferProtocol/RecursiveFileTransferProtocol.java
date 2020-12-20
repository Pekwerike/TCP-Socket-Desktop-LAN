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

        // initialize import collection variables with the filesCount read
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

        // read and save the bytes of each file received
        for(int i = 0; i < fileCount; i++){
            String currentFileName = filesName[i];
            int currentFileLength = filesLength[i];

            if(currentFileName.startsWith("Directory") && currentFileLength == 0){
                String folderName = currentFileName;
                // get the amount of file in this directory,
                // then read and save into the directory the byte of each file under the directory
                int filesCountInDirectory = filesCountInFolder.get(0);
                filesCountInFolder.remove(0); // move to the next
                int newFilesCount = i + filesCountInDirectory + 1;
                for(int j = i + 1; j < newFilesCount; j++){
                    if(filesName[j].startsWith("Directory")){
                        // reached new folder, hence break out of this inner loop
                        break;
                    }
                    FileOutputStream fileOS = new FileOutputStream(saveFileInFolder(folderName, filesName[j]));
                    byte[] buffer = null;
                    try{
                        buffer = new byte[filesLength[j]];
                    }catch (OutOfMemoryError outOfMemoryError){
                        buffer = new byte[1_000_000];
                    }
                    int unreadBytes = filesLength[j];
                    int readBytes = 0;

                    while(unreadBytes > 0){
                        readBytes = socketDIS.read(buffer, 0, Math.min(unreadBytes, buffer.length));
                        if(readBytes == -1){
                            // End of stream, reached
                            break;
                        }
                        fileOS.write(buffer, 0, readBytes);
                        unreadBytes -= readBytes;
                    }
                    fileOS.close();
                    // move to the next index
                    i = j;
                }

            }
        }
    }

    private static void saveNestedFiles(String folderName, ArrayList<File> files,
                                        ArrayList<String> filesName, ArrayList<Integer> filesLength,
                                        DataInputStream socketDIS, ArrayList<Integer> filesInFoldersCount) throws IOException {
        for(int i = 0; i < files.size(); i++){
            if(filesName.get(i).startsWith("Directory")){
                String newFolderName = filesName.get(i);
                // reached new folder, hence break out of this inner loop
                int folderFilesCount = filesInFoldersCount.get(0);
                filesInFoldersCount.remove(0);
                ArrayList<File> filesInFolder = new ArrayList<>();
                ArrayList<String> filesNameInFolder = new ArrayList<>();
                ArrayList<Integer> filesLengthInFolder = new ArrayList<>();
                for(int j = i + 1; j < i + folderFilesCount + 1; j++){
                    filesInFolder.add(files.get(j));
                    filesNameInFolder.add(filesName.get(j));
                    filesLengthInFolder.add(filesLength.get(j));
                    i = j;
                }
                saveNestedFiles(newFolderName,
                        filesInFolder,
                        filesNameInFolder,
                        filesLengthInFolder,
                        socketDIS,
                        filesInFoldersCount);
            }
            FileOutputStream fileOS = new FileOutputStream(saveFileInFolder(folderName, filesName.get(i)));
            byte[] buffer = null;
            try{
                buffer = new byte[filesLength.get(i)];
            }catch (OutOfMemoryError outOfMemoryError){
                buffer = new byte[1_000_000];
            }
            int unreadBytes = filesLength.get(i);
            int readBytes;

            while(unreadBytes > 0){
                readBytes = socketDIS.read(buffer, 0, Math.min(unreadBytes, buffer.length));
                if(readBytes == -1){
                    // End of stream, reached
                    break;
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

    private static File saveFileInFolder(String folderName, String fileName) throws IOException {
        File folder = new File("C:\\Users\\Prosper's PC\\Pictures\\" + folderName);
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, fileName);
    }
}
