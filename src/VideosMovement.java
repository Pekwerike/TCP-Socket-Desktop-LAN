import java.io.*;
import java.net.Socket;

public class VideosMovement {

    void transferVideo(File[] videoCollection, Socket clientSocket) throws IOException {
        OutputStream clientSocketOS = clientSocket.getOutputStream();
        BufferedOutputStream clientSocketBOS = new BufferedOutputStream(clientSocketOS);
        DataOutputStream clientSocketDOS = new DataOutputStream(clientSocketBOS);

        //write the amount of video to transfer
        clientSocketDOS.writeInt(videoCollection.length);

        // write the length and name of each video to the clientSocket DataOutputStream
        for(int i = 0; i < videoCollection.length; i++){
            clientSocketDOS.writeLong(videoCollection[i].length());
            clientSocketDOS.flush();
            clientSocketDOS.writeChars(videoCollection[i].getName());
        }

        // write the bytes of each video to the clientSocket DataOutputStream
        for(int i = 0; i < videoCollection.length; i++){
            FileInputStream videoFileInputStream = new FileInputStream(videoCollection[i]);
            byte[] buffer = videoFileInputStream.readAllBytes();
            clientSocketDOS.write(buffer);
        }

        clientSocketDOS.close();
    }

    void receiveVideos(){

    }
}
