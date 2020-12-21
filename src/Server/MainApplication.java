package Server;

import FileTransferProtocol.OptimizedFileTransferProtocol;
import FileTransferProtocol.GeneralizedFileTransferProtocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApplication {

   public static void main(String[] args) throws IOException {
        Runnable runnable = () -> {
            ServerSocket serverSocket = null;
            Socket client = null;
            try {
                serverSocket = new ServerSocket(8086);
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            };
            System.out.println("Connected to a client socket");

            File folder1 = getFolder("Lesson 3");
            File folder2 = getFolder("Lesson 4");

            File video1 = getVideoFile("ANDK L1 01 IntroVideo");
            File video2 = getVideoFile("L1 03 Dice Roller HSASC V4 V2");
            File video3 = getVideoFile("L1 06 Creating The Dice Roller Project HS-SC");
            File video4 = getVideoFile("L1 51 Adding The ImageView SC 1");
            File[] videoCollection = {folder1};

            OptimizedFileTransferProtocol fileTransferProtocol = new OptimizedFileTransferProtocol(client);
            GeneralizedFileTransferProtocol generalizedFileTransferProtocol = new GeneralizedFileTransferProtocol(client);
            //pause reading the receiveVideos function from calling, so the client will be able to write out
            // all the videos first
            try {
                //fileTransferProtocol.optimizedTransferFile(videoCollection);
                generalizedFileTransferProtocol.transferFiles(folder1);
               // videosMovement.receiveVideos();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

    }


    private static File getVideoFile(String name){
        return new File("C:\\Users\\Prosper's PC\\Desktop\\ANROID DEVELOPMENT COURSE\\LESSON 1\\" + name +".mp4");
    }

    private static File getFolder(String name){
       return new File("C:\\Users\\Prosper's PC\\Desktop\\KOTLIN BOOTCAMP\\" + name);
    }
}
