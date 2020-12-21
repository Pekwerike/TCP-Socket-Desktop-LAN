package FileTransferProtocol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class FileTransferUtils {

    public static Hashtable<String, Integer> directoryFilesCount(File folder){
        int filesCount = 0;
        Hashtable<String, Integer> directoryFilesCount = new Hashtable<>();
        File[] directoryFiles = folder.listFiles();
        assert directoryFiles != null;
        for (File directoryFile : directoryFiles) {
            if (directoryFile.isDirectory()) {
                filesCount += 1;
                Hashtable<String, Integer> innerDirectoryFilesCount = directoryFilesCount(directoryFile);
                for (Map.Entry<String, Integer> entry : innerDirectoryFilesCount.entrySet()) {
                    directoryFilesCount.put(entry.getKey(), entry.getValue());
                }
                int innerCount = innerDirectoryFilesCount.get(directoryFile.getName());
                filesCount += innerCount;
            } else {
                filesCount += 1;
            }
        }
        directoryFilesCount.put(folder.getName(), filesCount);
        return directoryFilesCount;
    }

    public static ArrayList<File> straightenFolderIntoList
            (File folder) {
        ArrayList<File> filesToReturn = new ArrayList<>();
        File[] filesInThisFolder = folder.listFiles();
        filesToReturn.add(folder);

        for (int i = 0; i < filesInThisFolder.length; i++) {
            if (filesInThisFolder[i].isDirectory()) {
                filesToReturn.addAll(straightenFolderIntoList(filesInThisFolder[i]));
            } else {
                filesToReturn.add(filesInThisFolder[i]);
            }
        }
        return filesToReturn;
    }

    public static int getFilesCount(File folder) {
        int filesCount = 1;
        File[] filesInFolder = folder.listFiles();

        for (int i = 0; i < filesInFolder.length; i++) {
            if (filesInFolder[i].isDirectory()) {
                filesCount += getFilesCount(filesInFolder[i]);
            } else {
                filesCount += 1;
            }
        }
        return filesCount;
    }



    public static File saveFileInFolder(String folderName, String fileName) throws IOException {
        File folder = new File("C:\\Users\\Prosper's PC\\Pictures\\" + folderName);
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, fileName);
    }
}
