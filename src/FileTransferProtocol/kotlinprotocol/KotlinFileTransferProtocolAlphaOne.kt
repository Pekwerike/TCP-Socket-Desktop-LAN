package FileTransferProtocol.kotlinprotocol

import java.io.*
import java.net.Socket
import java.net.SocketException
import java.util.Collections.min
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

class KotlinFileTransferProtocolAlphaOne(private val socket: Socket) {

    fun transferFolder(folder: File) {
        val socketOS = socket.getOutputStream()
        val socketBOS = BufferedOutputStream(socketOS)
        val socketDOS = DataOutputStream(socketBOS)

        val files = folder.listFiles().toMutableList()

        socketDOS.writeInt(files.size)

        files.forEach {
            socketDOS.writeUTF(it.name)
            socketDOS.writeLong(it.length())


            val fileIS = FileInputStream(it)
            val bufferArray = ByteArray(10_000_000)
            var lengthRead: Int


            while (fileIS.read(bufferArray).also { lengthRead = it } > 0) {
                socketDOS.write(bufferArray, 0, lengthRead)
            }
            fileIS.close()
        }
    }

    fun receiveFolder(parentFolder: File) {
        val socketDIS = DataInputStream(BufferedInputStream(socket.getInputStream()))


        val filesSent = socketDIS.readInt()

        for (i in 0 until filesSent) {
            val fileName = socketDIS.readUTF()
            var fileLength = socketDIS.readLong()

            val fileToSave = File(parentFolder, fileName)
            val fileOutputStream = FileOutputStream(fileToSave)
            val bufferArray = ByteArray(10_000_000)


            while (fileLength > 0) {
                try {
                    val bytesRead = socketDIS.read(bufferArray, 0, min(fileLength.toInt(), bufferArray.size))
                    if (bytesRead == -1) break
                    fileOutputStream.write(bufferArray)
                    fileLength -= bytesRead
                } catch (connectionReset: SocketException) {
                    break
                }
            }
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }
}

