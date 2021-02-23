package FileTransferProtocol.kotlinprotocol

import java.io.*
import java.net.Socket

class KoltinFileTransferProtocolAlphaThree(private val socket: Socket) {

    private val socketOS = socket.getOutputStream()
    private val socketBOS = BufferedOutputStream(socketOS)
    private val socketDOS = DataOutputStream(socketBOS)
    private val socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))

    fun transferFile(baseFolder: File) {
        socketDOS.writeUTF(baseFolder.name)
        val filesInFolder = baseFolder.listFiles()

        socketDOS.writeInt(filesInFolder.size)

        filesInFolder.forEach { file ->
            if(file.isDirectory){
                socketDOS.writeUTF(file.name + "Directory")
                transferFile(file)
            }

            socketDOS.writeUTF(file.name)
            socketDOS.writeLong(file.length())

            val fileIS = FileInputStream(file)
            val bufferArray = ByteArray(5_000_000)
            var lengthRead : Int

            while(fileIS.read(bufferArray).also { lengthRead = it } > 0){
                socketDOS.write(bufferArray, 0, lengthRead)
            }
            fileIS.close()
        }
    }

    fun receiveFile(baseFolder: File){
        val newBaseFolderName = socketDIS.readUTF()
        val newBaseFolder = File(baseFolder, newBaseFolderName)
        newBaseFolder.mkdirs()

        val numberOfFilesInBaseFolder = socketDIS.readInt()

        for(i in 0 until numberOfFilesInBaseFolder){
            val fileName = socketDIS.readUTF()
            if(fileName.endsWith("Directory")){
                receiveFile(newBaseFolder)
            }
            val fileLength
        }
    }
}