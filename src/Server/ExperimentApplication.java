package Server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.function.Consumer;

public class ExperimentApplication {

    public static void main(String[] args){
        File folder1 = getFolder("Lesson 3");
      //  System.out.println(getFilesCount(folder1));
        ArrayList<File> allFilesInFolder = straightenFiles(folder1);
        Hashtable<String, Integer> directoryFilesCount = directoryFilesCount2(folder1);

    }

    private static Hashtable<String, Integer> directoryFilesCount2(File folder){
        int filesCount = 0;
        Hashtable<String, Integer> directoryFilesCount = new Hashtable<>();
        File[] directoryFiles = folder.listFiles();
        for(int i = 0; i < directoryFiles.length; i++){
            if(directoryFiles[i].isDirectory()){
                Hashtable<String, Integer> innerDirectoryFilesCount = directoryFilesCount2(directoryFiles[i]);
                int innerCount = innerDirectoryFilesCount.get(directoryFiles[i].getName());
                directoryFilesCount.put(directoryFiles[i].getName(), innerCount);
                filesCount += innerCount;
            }else{
                filesCount += 1;
            }
        }
        directoryFilesCount.put(folder.getName(), filesCount);
        return directoryFilesCount;
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

    private static int getFilesCount(File folder){
        int filesCount = 1;
        File[] filesInFolder = folder.listFiles();
        Arrays.sort(filesInFolder, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return Long.valueOf(file.lastModified()).compareTo(t1.lastModified());
            }
        });

        for(int i = 0; i < filesInFolder.length; i++){
            if(filesInFolder[i].isDirectory()){
                filesCount += getFilesCount(filesInFolder[i]);
            }else {
                System.out.println(filesInFolder[i].getName());
                filesCount += 1;
            }
        }
        return filesCount;
    }

    private static File getFolder(String name){
        return new File("C:\\Users\\Prosper's PC\\Desktop\\KOTLIN BOOTCAMP\\" + name);
    }
}
