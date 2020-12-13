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

    private static void transferSingleImage(Socket clientOS, DataOutputStream clientDOS) throws IOException {
        FileInputStream imageFileIS = new FileInputStream("C:\\Users\\Prosper's PC\\Pictures\\Screenshot_20200723_153336_com.prosperekwerike.pulsar.jpg");
        FileInputStream secondImageFileIS = new FileInputStream("C:\\Users\\Prosper's PC\\Pictures\\Screenshot_20200723_153307_com.prosperekwerike.pulsar.jpg");
        FileInputStream[] arrayOfIS = {imageFileIS, secondImageFileIS};


        //transfer image to client

        DataInputStream imageFileDIS = new DataInputStream(imageFileIS);

        byte[] buffer = imageFileDIS.readAllBytes();

        long imageLength = buffer.length;
        clientDOS.writeLong(imageLength);
        clientDOS.write(buffer);

        imageFileIS.close();
        clientOS.close();
    }

    private static File createFile() {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\serverc" + System.currentTimeMillis() + ".jpg");
    }
}
