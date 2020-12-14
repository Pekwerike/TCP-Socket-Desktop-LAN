package Client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientMainApplication {

    public static void main(String[] args) throws IOException {
        Socket server = new Socket("192.168.43.190", 8085);
        System.out.println("Connected to server");

        File video1 = getVideoFile("148 Introduction to the DOM");
        File video2 = getVideoFile("149 Defining the DOM");
        File video3 = getVideoFile("150 Select and Manipulate");
        File video4 = getVideoFile("152 Important Selector Methods");
        File[] videoCollection = {video1, video2, video3, video4};


        ClientVideosMovement videosMovement = new ClientVideosMovement(server);
        videosMovement.transferVideo(videoCollection);
        //videosMovement.receiveVideo();

    }


    private static File getVideoFile(String name){
        return new File("C:\\Users\\Prosper's PC\\Desktop\\the web developer bootcamp\\13 DOM Manipulation\\" + name +".mp4");
    }
}
