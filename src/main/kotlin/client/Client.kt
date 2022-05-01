package client

import Message
import java.io.OutputStream
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class Client(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true
    private var nick: String = ""

    init {
        println("Connected to server at $address on port $port")
    }

    private val reader: InputStream = connection.getInputStream()
    private val writer: OutputStream = connection.getOutputStream()

    fun run() {
        thread { read() }
        println("Nick: ")
        nick = readLine() ?: ""
        write(Message(nick, "|my nick is $nick|",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
        println("Welcome to the club, buddy!\n" +
                "To Exit, write: '-exit-'.\n" +
                "Chat:\n")
        while (connected) {
            //val input = readLine() ?: ""
            var tempInput = ""
            val inputList = mutableListOf<String>()
            while ("-send-" != tempInput) {
                tempInput = readLine() ?: ""
                inputList.add(tempInput)
            }
            inputList.remove("-send-")
            val input = inputList.joinToString("\n")
            if ("-exit-" in input) {
                connected = false
                reader.close()
                connection.close()
            } else {
                val answer = Message(nick, input,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                write(answer)
            }
        }
    }

    private fun write(message: Message) {
        writer.write((message.checkForFile()).toByteArray().size shr 24)
        writer.write((message.checkForFile()).toByteArray().size shr 16)
        writer.write((message.checkForFile()).toByteArray().size shr 8)
        writer.write((message.checkForFile()).toByteArray().size)
        writer.write((message.checkForFile()).toByteArray())
        if (message.file != null) writer.write(message.file!!)
    }

    private fun createDir(): String {
        val directory = File(
            System.getProperty("user.home") +
                    File.separator + "Desktop" +
                    File.separator + nick
        )
        if (!directory.exists()) directory.mkdir()
        return directory.absolutePath
    }

    private fun writeNewFile(fileName: String, file: ByteArray): String {
        val resultFile = File("${createDir()}${File.separator}$fileName")
        resultFile.createNewFile()
        val fos = FileOutputStream(resultFile)
        fos.write(file)
        fos.close()
        return resultFile.absolutePath.replace("\\", "***")
    }

    private fun getFile(size: Int, stream: InputStream): ByteArray {
        return stream.readNBytes(size)
    }

    private fun read() {
        while (connected) {
            var size = 0
            for (i in 0 until 4 ) {
                size = size shl 8
                size += reader.read() and 0xff
            }
            val msgBytes = ByteArray(size)
            for (i in 0 until size) {
                msgBytes[i] = (reader.read().toByte())
            }
            val result = """:::""".toRegex().split(msgBytes.decodeToString())
            if (result.size > 3) {
                println(result[0] + "|"
                        + result[1] + "|"
                        + result[2]
                    .replace("\\:", ":")
                    .replace("""file-:[^ ]*""".toRegex(), "[file]" + writeNewFile(result[3], getFile(result[4].toInt(), reader)).replace("***", "\\\\"))
                        )
            }
            else println(result[0] + "|" + result[1] + "|" + result[2].replace("\\:", ":"))
        }
    }
}