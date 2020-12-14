package Client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientMainApplication {

    public static void main(String[] args) throws IOException {
        Socket server = new Socket("192.168.43.190", 8085);
        System.out.println("Connected to server");

        ClientVideosMovement videosMovement = new ClientVideosMovement(server);
        videosMovement.receiveVideo();

    }


    private static File createFile() {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\new" + System.currentTimeMillis() + ".jpg");
    }
}
