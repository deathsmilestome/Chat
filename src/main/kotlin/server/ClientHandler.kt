package server

import Message
import java.io.OutputStream
import java.net.Socket
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


class ClientHandler(private val client: Socket, private val otherClients: Set<ClientHandler>) {
    private val reader: InputStream = client.getInputStream()
    private var running: Boolean = false
    private var nick: String = ""

    fun run() {
        running = true
        thread { read() }
        while (running) {
            val input = readLine() ?:""
            if ("-exit-" in input) shutdown()
        }
    }

    private fun write(message: Message) {
        otherClients.forEach { client ->
            if (client.nick != this.nick) {
                val writer: OutputStream = client.client.getOutputStream()
                writer.write((message.checkForFile()).toByteArray().size shr 24)
                writer.write((message.checkForFile()).toByteArray().size shr 16)
                writer.write((message.checkForFile()).toByteArray().size shr 8)
                writer.write((message.checkForFile()).toByteArray().size)
                writer.write((message.checkForFile()).toByteArray())
                if (message.file != null) {
                    writer.write(message.file!!)
                }
            }
        }
    }

    private fun getFile (size: Int): ByteArray {
        val fileBytes = ByteArray(size)
        for (i in 0 until size) {
            fileBytes[i] = reader.read().toByte()
        }
        return fileBytes
    }

    private fun read() {
        var msg: Message
        while (running) {
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
            if (result[2].startsWith("|my nick is")) nick = result[1]
            if (result[2] == "EXIT") shutdown()
            msg = if (result.size > 3) {
                Message(result[1], result[2],
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    result[3], result[4].toInt(), getFile(result[4].toInt()))
            } else {
                Message(result[1], result[2], LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            }
            write(msg)
        }
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }
}