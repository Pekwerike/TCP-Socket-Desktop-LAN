package Client;


import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaOne;
import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaThree;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientMainApplication {

    public static void main(String[] args) throws IOException {
        Runnable runnable = () -> {
            Socket server = null;
            boolean scanning = true;
            while (scanning) {
                try {
                    server = new Socket("192.168.43.190", 8086);
                   // server.connect(new InetSocketAddress("192.168.43.190", 8086), 500000);
                    scanning = false;
                    //  server = new Socket("192.168.137.1", 8086);
                    // server = new Socket("10.6.130.252", 8086);
                    System.out.println("Connected to server");
                } catch (Exception e) {
                  continue;
                }
            }

            assert server != null;
            KotlinFileTransferProtocolAlphaThree kotlinFileTransferProtocolAlphaThree = new KotlinFileTransferProtocolAlphaThree(server);
            kotlinFileTransferProtocolAlphaThree.receiveFile(getVideoFile(""));
         //   kotlinFileTransferProtocolAlphaThree.transferFile(getVideoFile("Camera Roll"));

        };

        Thread thread = new Thread(runnable);
        thread.start();

    }


    private static File getVideoFile(String folderName) {
        if (folderName.equals(" "))
            return new File("C:\\Users\\Prosper's PC\\Pictures");
        else return new File("C:\\Users\\Prosper's PC\\Pictures\\" + folderName);
    }
}
