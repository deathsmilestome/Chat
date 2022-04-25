package client

import Message
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread
import java.io.File
import java.io.FileOutputStream


class Client(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true
    var nick: String = ""

    init {
        println("Connected to server at $address on port $port")
    }

    private val reader: Scanner = Scanner(connection.getInputStream())
    private val writer: OutputStream = connection.getOutputStream()

    fun run() {
        thread { read() }
        println("Nick: ")
        nick = readLine() ?: ""
        write(nick)
        println("Welcome to the club, buddy!\n" +
                "To Exit, write: 'EXIT'.\n" +
                "Chat:\n")
        while (connected) {
            val input = readLine() ?: ""
            if ("exit" in input) {
                connected = false
                reader.close()
                connection.close()
            } else {
                val answer = Message(nick, input,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).checkForFile()
                write(answer)
            }
        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
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
        return resultFile.absolutePath
    }

    private fun read() {
        while (connected) {
            val result = """:::""".toRegex().split(reader.nextLine())
            println(result[5])
            if (result.size > 3) {
                println(result[0] + "|"
                        + result[1] + "|"
                        + result[2]
                    .replace("\\:", ":")
                    .replace("""file-:.*""".toRegex(), "[file]")
                        + writeNewFile(result[3], result[5].toByteArray()))
            }
            else println(result[0] + "|" + result[1] + "|" + result[2].replace("\\:", ":"))
        }
    }
}