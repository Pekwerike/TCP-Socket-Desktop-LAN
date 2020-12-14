package Server;

import java.io.*;
import java.net.Socket;

public class VideosMovement {
    private Socket mClientSocket;

    public VideosMovement(Socket clientSocket){
        this.mClientSocket = clientSocket;
    }

    void transferVideo(File[] videoCollection) throws IOException {
        OutputStream clientSocketOS = mClientSocket.getOutputStream();
        BufferedOutputStream clientSocketBOS = new BufferedOutputStream(clientSocketOS);
        DataOutputStream clientSocketDOS = new DataOutputStream(clientSocketBOS);

        //write the amount of video to transfer
        clientSocketDOS.writeInt(videoCollection.length);

        // write the length and name of each video to the clientSocket DataOutputStream
        for(int i = 0; i < videoCollection.length; i++){
            clientSocketDOS.writeLong(videoCollection[i].length());
            clientSocketDOS.writeUTF(videoCollection[i].getName());
        }

        // write the bytes of each video to the clientSocket DataOutputStream
        for(int i = 0; i < videoCollection.length; i++){
            FileInputStream videoFileInputStream = new FileInputStream(videoCollection[i]);
            byte[] buffer = videoFileInputStream.readAllBytes();
            clientSocketDOS.write(buffer);
        }

        clientSocketDOS.close();
    }

    void receiveVideos() throws IOException {
        InputStream clientSocketIS = mClientSocket.getInputStream();
        BufferedInputStream clientSocketBIS = new BufferedInputStream(clientSocketIS);
        DataInputStream clientSocketDIS = new DataInputStream(clientSocketBIS);

        // read the amount of videos sent by the client
        int videoCount = clientSocketDIS.readInt();

        int[] videosLength = new int[videoCount];
        String[] videosName = new String[videoCount];

        // read the length and name for each video sent by the client
        for(int i = 0; i < videoCount; i++){
           videosLength[i] = (int) clientSocketDIS.readLong();
           videosName[i] = clientSocketDIS.readUTF();
        }

        // read out the bytes for each video sent by the client
        for(int i = 0; i < videoCount; i++){
            FileOutputStream receivedFileOS = new FileOutputStream(createFile(videosName[i]));
            byte[] buffer = new byte[videosLength[i]];
            clientSocketDIS.read(buffer, 0, videosLength[i]);
            receivedFileOS.write(buffer);
            receivedFileOS.close();
        }
        System.out.println(String.format("Server received %d videos", videoCount));
    }

    private static File createFile(String videoName) {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\" + videoName + ".mp4");
    }

}
