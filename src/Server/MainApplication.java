package Server;

import FileTransferProtocol.OptimizedFileTransferProtocol;
import FileTransferProtocol.GeneralizedFileTransferProtocol;
import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaOne;
import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaThree;



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
            }

            System.out.println("Connected to a client socket");

            File folder1 = getFolder("ZipBolt");

            KotlinFileTransferProtocolAlphaThree kotlinFileTransferProtocolAlphaThree = new KotlinFileTransferProtocolAlphaThree(client);
            kotlinFileTransferProtocolAlphaThree.transferFile(folder1);
         //   kotlinFileTransferProtocolAlphaThree.transferFile(getFolder("Canary"));


        };

        Thread thread = new Thread(runnable);
        thread.start();

    }


    private static File getFolder(String name) {
        if(!name.equals(" "))
        return new File("C:\\Users\\Prosper's PC\\Desktop\\" + name);
        else return new File("C:\\Users\\Prosper's PC\\Desktop");
    }
}
