package Server

import java.io.*
import java.net.Socket

class ImagesMovement {

    @Throws(IOException::class)
    fun transferVideo(videoCollection: Array<File>, clientSocket: Socket) {
        val clientSocketOS = clientSocket.getOutputStream()
        val clientSocketBOS = BufferedOutputStream(clientSocketOS)
        val clientSocketDOS = DataOutputStream(clientSocketBOS)

        //write the amount of video to transfer
        clientSocketDOS.writeInt(videoCollection.size)

        // write the length and name of each video to the clientSocket DataOutputStream
        for (i in videoCollection.indices) {
            clientSocketDOS.writeLong(videoCollection[i].length())
            clientSocketDOS.writeUTF(videoCollection[i].name)
        }

        // write the bytes of each video to the clientSocket DataOutputStream
        for (i in videoCollection.indices) {
            val videoFileInputStream = FileInputStream(videoCollection[i])
            val buffer = videoFileInputStream.readBytes()
            clientSocketDOS.write(buffer)
            videoFileInputStream.close()
        }
        clientSocketDOS.close()
        println("Done")
    }

}