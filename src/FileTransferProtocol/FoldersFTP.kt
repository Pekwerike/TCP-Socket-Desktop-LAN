package FileTransferProtocol;

import java.io.*
import java.net.Socket
import kotlin.math.min

class FoldersFTP(private val socket: Socket) {
    private var filesCount = 0
    private val filesDTO: MutableList<FileDTO> = mutableListOf()

    fun transferFolder(folder: File) {
        val socketOS = socket.getOutputStream()
        val socketBOS = BufferedOutputStream(socketOS)
        val socketDOS = DataOutputStream(socketBOS)

        folderCount(folder) // get the total number of files to send and group both folders and files into a filesDTO
        socketDOS.write(filesCount) // number of files to send


        filesDTO.forEach { fileDTO ->
            socketDOS.writeUTF(fileDTO.name)
            if (fileDTO.name.endsWith("Folder")) {
                socketDOS.writeLong(fileDTO.childCount)
            } else {
                socketDOS.writeLong(fileDTO.length)

                val fileInputStream = FileInputStream(fileDTO.file!!)
                val bufferArray = ByteArray(5_000_000)

                var lengthOfBuffer: Int

                while (fileInputStream.read(bufferArray).also { lengthOfBuffer = it } > 0) {
                    socketDOS.write(bufferArray, 0, lengthOfBuffer)
                }
                fileInputStream.close()
            }
        }
    }

    private fun folderCount(file: File) {
        // increment the files count by one, for a new file
        filesCount += 1

        if (file.isDirectory) {
            val filesInDirectory = file.listFiles()!!
            // add directory to the list of filesDTO
            filesDTO.add(FileDTO(name = file.name + "Folder", childCount = filesInDirectory.size.toLong(), length = 0, file = null))
            // for every file in the directory, count them, and add them to the list of filesDTO
            filesInDirectory.forEach {
                folderCount(it)
            }
        } else {
            // add file directory to the list of filesDTO
            filesDTO.add(FileDTO(name = file.name, childCount = 0, length = file.length(), file = file))
        }
    }

    fun receiveFolder(parentFolder: File) {
        val socketIS = socket.getInputStream()
        val socketBIS = BufferedInputStream(socketIS)
        val socketDIS = DataInputStream(socketBIS)

        filesCount = socketDIS.read()

        while (filesCount > 0) {
            readFiles(parentFolder, socketDIS)
        }
        socketDIS.close()
    }

    private fun readFiles(parentFolder: File, socketDIS: DataInputStream) {
        var fileName = socketDIS.readUTF()
        System.out.println("Read out file name $fileName")

        if (fileName.endsWith("Folder")) {
            fileName = fileName.removeSuffix("Folder")
            val newParentFolder = File(parentFolder, fileName)
            if (!newParentFolder.exists()) newParentFolder.mkdirs()

            val childCount = socketDIS.readLong()

            for (i in 0 until childCount) {
                readFiles(newParentFolder, socketDIS)
            }
        } else {
            var lengthOfDataForFile = socketDIS.readLong()
            val fileOS = FileOutputStream(File(parentFolder, fileName))
            val bufferArray = ByteArray(5_000_000)

            while (lengthOfDataForFile > 0) {
                val lengthOfDataRead = socketDIS.read(bufferArray)
                fileOS.write(bufferArray, 0, min(lengthOfDataForFile.toInt(), bufferArray.size))
                lengthOfDataForFile -= lengthOfDataRead
            }
            fileOS.close()
        }
    }


}