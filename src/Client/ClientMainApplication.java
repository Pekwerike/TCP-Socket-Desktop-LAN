package Client;

import FileTransferProtocol.FoldersFTP;
import FileTransferProtocol.OptimizedFileTransferProtocol;
import FileTransferProtocol.GeneralizedFileTransferProtocol;
import FileTransferProtocol.kotlinprotocol.KotlinFileTransferProtocolAlphaOne;

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


            KotlinFileTransferProtocolAlphaOne kotlinFileTransferProtocolAlphaOne = new KotlinFileTransferProtocolAlphaOne(server);
            kotlinFileTransferProtocolAlphaOne.receiveFolder(getVideoFile());

           // OptimizedFileTransferProtocol ftp = new OptimizedFileTransferProtocol(server);
           // GeneralizedFileTransferProtocol generalizedFileTransferProtocol = new GeneralizedFileTransferProtocol(server);
           /*FoldersFTP foldersFTP = new FoldersFTP(server);
           foldersFTP.receiveFolder(getVideoFile());*/

           /* GeneralizedFileTransferProtocol generalizedFileTransferProtocol = new GeneralizedFileTransferProtocol(server);
            try {
                generalizedFileTransferProtocol.receiveFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        };

        Thread thread = new Thread(runnable);
        thread.start();

    }


    private static File getVideoFile(){
        return new File("C:\\Users\\Prosper's PC\\Pictures\\ZipBolt");
    }
}
