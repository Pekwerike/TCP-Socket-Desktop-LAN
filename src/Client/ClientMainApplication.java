package Client;



import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaOne;
import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaThree;


import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientMainApplication {

    public static void main(String[] args) throws IOException {
        Runnable runnable = () -> {
            Socket server = null;
            try {
                server = new Socket("192.168.43.190", 8086);
                System.out.println("Connected to server");
            } catch (IOException e) {
                e.printStackTrace();
            }



            assert server != null;
            KotlinFileTransferProtocolAlphaThree kotlinFileTransferProtocolAlphaThree = new KotlinFileTransferProtocolAlphaThree(server);
            kotlinFileTransferProtocolAlphaThree.receiveFile(getVideoFile());

        };

        Thread thread = new Thread(runnable);
        thread.start();

    }


    private static File getVideoFile(){
        return new File("C:\\Users\\Prosper's PC\\Pictures\\");
    }
}
