package Server;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ExperimentApplication {

    public static void main(String[] args){
        File folder1 = getFolder("Lesson 3");
        System.out.println(getFilesCount(folder1));

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
