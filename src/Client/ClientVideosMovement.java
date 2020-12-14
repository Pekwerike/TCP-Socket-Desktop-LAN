package Client;

import java.io.*;
import java.net.Socket;

public class ClientVideosMovement {
    private Socket mServer;

    public ClientVideosMovement(Socket server){
        this.mServer = server;
    }

    void transferVideo(File[] videoCollection) throws IOException {
        OutputStream serverOS = mServer.getOutputStream();
        BufferedOutputStream serverBOS = new BufferedOutputStream(serverOS);
        DataOutputStream serverDOS = new DataOutputStream(serverBOS);

        // write the amount of videos to send
        serverDOS.writeInt(videoCollection.length);

        // write the length and name of each video
        for(int i = 0; i < videoCollection.length; i++){
            serverDOS.writeLong(videoCollection[i].length());
            serverDOS.writeUTF(videoCollection[i].getName());
        }

        // write the byte of each video to the Server DataOutputStream
        for(int i = 0; i < videoCollection.length; i++){
            FileInputStream videoInputStream = new FileInputStream(videoCollection[i]);
            byte[] buffer = videoInputStream.readAllBytes();
            serverDOS.write(buffer);
            videoInputStream.close();
        }

        System.out.println(String.format("Sent %d videos to server", videoCollection.length));
        serverDOS.close();
    }

    void receiveVideo() throws IOException {
        InputStream serverIS = mServer.getInputStream();
        BufferedInputStream serverBIS = new BufferedInputStream(serverIS);
        DataInputStream serverDIS = new DataInputStream(serverBIS);

        // read the number of video file sent
        int totalFiles = serverDIS.readInt();

        int[] videosSize = new int[totalFiles];
        String[] videosName = new String[totalFiles];

        // read the names and length for each video
        for (int i = 0; i < totalFiles; i++) {
            videosSize[i] = (int) serverDIS.readLong();
            videosName[i] = serverDIS.readUTF();
        }

        // read the bytes for each video
        for (int i = 0; i < totalFiles; i++) {
            byte[] buffer = new byte[videosSize[i]];
            serverDIS.read(buffer, 0, videosSize[i]);
            FileOutputStream receivedVideo = new FileOutputStream(createFile(videosName[i]));
            receivedVideo.write(buffer);
            receivedVideo.close();
        }
        System.out.println(String.format("%d videos received", totalFiles));

    }

    private static File createFile(String videoName) {
        return new File("C:\\Users\\Prosper's PC\\Pictures\\" + videoName + ".mp4");
    }
}
