package FileTransferProtocol.kotlinprotocol

import java.io.*
import java.net.Socket
import kotlin.math.min

class KotlinFileTransferProtocolAlphaThree(socket: Socket) {

    private val socketOS = socket.getOutputStream()
    private val socketBOS = BufferedOutputStream(socketOS)
    private val socketDOS = DataOutputStream(socketBOS)
    private val socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))

    fun transferFile(baseFolder: File) {
        socketDOS.writeUTF(baseFolder.name)
        val filesInFolder = baseFolder.listFiles()

        socketDOS.writeInt(filesInFolder.size)

        filesInFolder.forEach { file ->
            if (file.isDirectory) {
                socketDOS.writeUTF(file.name + "Directory")
                transferFile(file)
            } else {

                socketDOS.writeUTF(file.name)
                socketDOS.writeLong(file.length())

                val fileIS = FileInputStream(file)
                val bufferArray = ByteArray(5_000_000)
                var lengthRead: Int

                while (fileIS.read(bufferArray).also { lengthRead = it } > 0) {
                    socketDOS.write(bufferArray, 0, lengthRead)
                }
                fileIS.close()
            }
        }
    }

    fun receiveFile(baseFolder: File){
        val newBaseFolderName = socketDIS.readUTF()
        val newBaseFolder = File(baseFolder, newBaseFolderName)
        newBaseFolder.mkdirs()

        val numberOfFilesInBaseFolder = socketDIS.readInt()

        for(i in 0 until numberOfFilesInBaseFolder) {
            val fileName = socketDIS.readUTF()
            if (fileName.endsWith("Directory")) {
                receiveFile(newBaseFolder)
            } else {
                var fileLength = socketDIS.readLong()

                val fileToSave = File(newBaseFolder, fileName)
                val fileOutputStream = FileOutputStream(fileToSave)
                val bufferArray = ByteArray(5_000_000)

                while (fileLength > 0) {
                    val bytesRead = socketDIS.read(bufferArray, 0, min(fileLength.toInt(), bufferArray.size))
                    if (bytesRead == -1) break
                    fileOutputStream.write(bufferArray)
                    fileLength -= bytesRead
                }
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }
    }
}