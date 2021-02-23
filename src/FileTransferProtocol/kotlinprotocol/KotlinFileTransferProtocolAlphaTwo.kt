package FileTransferProtocol.kotlinprotocol

import java.io.*
import java.net.Socket
import kotlin.math.min

class KotlinFileTransferProtocolAlphaTwo (private val socket : Socket){

    val socketOS = socket.getOutputStream()
    val socketBOS = BufferedOutputStream(socketOS)
    val socketDOS = DataOutputStream(socketBOS)
    val socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))

    fun transferFolder(folder : File){
        val files = folder.listFiles().toMutableList()


        socketDOS.writeInt(files.size)
        socketDOS.writeUTF(folder.name)

        files.forEach {
            if(it.isDirectory){
                socketDOS.writeUTF(it.name + "Directory")
                transferFolder(it)
            }
            socketDOS.writeUTF(it.name)
            socketDOS.writeLong(it.length())


            val fileIS = FileInputStream(it)
            val bufferArray = ByteArray(5_000_000)
            var lengthRead : Int

            while(fileIS.read(bufferArray).also { lengthRead = it } > 0){
                socketDOS.write(bufferArray, 0, lengthRead)
            }
            fileIS.close()
        }
    }

    fun receiveFolder(baseFolder : File){


        val filesSent = socketDIS.readInt()


        for(i in 0 until filesSent){
            val fileName = socketDIS.readUTF()
            if(fileName.endsWith("Directory")){
                val parentFolder = File(baseFolder, fileName)
                parentFolder.mkdirs()
                receiveFolder(parentFolder)
            }
            var fileLength = socketDIS.readLong()

            val fileToSave = File(baseFolder, fileName)

            val fileOutputStream = FileOutputStream(fileToSave)
            val bufferArray= ByteArray(5_000_000)

            while(fileLength > 0){
                val bytesRead = socketDIS.read(bufferArray, 0, min(fileLength.toInt(), bufferArray.size))
                if(bytesRead == -1) break
                fileOutputStream.write(bufferArray)
                fileLength -= bytesRead
            }
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }
}