package Client;

import FileTransferProtocol.FileTransferProtocol;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientMainApplication {

    public static void main(String[] args) throws IOException {
        Runnable runnable = () -> {
            Socket server = null;
            try {
                server = new Socket("192.168.43.190", 8086);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connected to server");

            File video1 = getVideoFile("ANDK L1 01 IntroVideo");
            File video2 = getVideoFile("L1 03 Dice Roller HSASC V4 V2");
            File video3 = getVideoFile("L1 06 Creating The Dice Roller Project HS-SC");
      /*      File video4 = getVideoFile("L1 51 Adding The ImageView SC 1");
            File video5 = getVideoFile("L1 67 Recap HS-A");
            File video6 = getVideoFile("L1 55 Student Interview HS");
            File video7 = getVideoFile("L1 26 Student Interview HS");*/
            File[] videoCollection = {video1, video2, video3};


            FileTransferProtocol ftp = new FileTransferProtocol(server);
            try {
                //videosMovement.transferVideo(videoCollection);
                ftp.optimizedReceiveFile();
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
}
