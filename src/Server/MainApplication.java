package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApplication {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8085);
        Socket client = serverSocket.accept();
        System.out.println("Connected to a client socket");


        File video1 = getVideoFile("ANDK L1 01 IntroVideo");
        File video2 = getVideoFile("L1 03 Dice Roller HSASC V4 V2");
        File video3 = getVideoFile("L1 06 Creating The Dice Roller Project HS-SC");
        File video4 = getVideoFile("L1 51 Adding The ImageView SC 1");
        File[] videoCollection = {video1, video2, video3, video4};

        VideosMovement videosMovement = new VideosMovement(client);
        //videosMovement.transferVideo(videoCollection);
        videosMovement.receiveVideos();

    }


    private static File getVideoFile(String name){
        return new File("C:\\Users\\Prosper's PC\\Desktop\\ANROID DEVELOPMENT COURSE\\LESSON 1\\" + name +".mp4");
    }
}
