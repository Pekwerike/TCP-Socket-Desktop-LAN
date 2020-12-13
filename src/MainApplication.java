import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApplication {

    public static void main(String[] args) throws IOException {
        //transferSingleImage();
        ServerSocket serverSocket = new ServerSocket(8085);
        Socket client = serverSocket.accept();
        System.out.println("Connected to a client socket");

        OutputStream clientOS = client.getOutputStream();
        BufferedOutputStream clientBOS = new BufferedOutputStream(clientOS);
        DataOutputStream clientDOS = new DataOutputStream(clientBOS);

        File video1 = getVideoFile("ANDK L1 01 IntroVideo");
        File video2 = getVideoFile("L1 03 Dice Roller HSASC V4 V2");
        File video3 = getVideoFile("L1 06 Creating The Dice Roller Project HS-SC");
        File video4 = getVideoFile("L1 51 Adding The ImageView SC 1");
        File[] videoCollection = {video1, video2, video3, video4};

        VideosMovement videosMovement = new VideosMovement();
        videosMovement.transferVideo(videoCollection, client);

        FileInputStream imageFileIS = new FileInputStream("C:\\Users\\Prosper's PC\\Pictures\\Screenshot_20200723_153336_com.prosperekwerike.pulsar.jpg");
        FileInputStream secondImageFileIS = new FileInputStream("C:\\Users\\Prosper's PC\\Pictures\\Screenshot_20200723_153307_com.prosperekwerike.pulsar.jpg");
        FileInputStream thirdImageFileIS = new FileInputStream("C:\\Users\\Prosper's PC\\Pictures\\Screenshot_20200723_153414_com.prosperekwerike.pulsar.jpg");
        byte[][] buffers = {imageFileIS.readAllBytes(), secondImageFileIS.readAllBytes(), thirdImageFileIS.readAllBytes()};


        clientDOS.writeInt(buffers.length);
        //send the file lengthst to the client
        for (int i = 0; i < buffers.length; i++) {
            clientDOS.writeLong(buffers[i].length);
        }

        for (int i = 0; i < buffers.length; i++) {
            byte[] buffer = buffers[i];
            clientDOS.write(buffer);
        }
        clientDOS.close();
        return;

    }

    private static File createFile() {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\serverc" + System.currentTimeMillis() + ".jpg");
    }

    private static File getVideoFile(String name){
        return new File("C:\\Users\\Prosper's PC\\Desktop\\ANROID DEVELOPMENT COURSE\\LESSON 1" + name +".mp4");
    }
}
