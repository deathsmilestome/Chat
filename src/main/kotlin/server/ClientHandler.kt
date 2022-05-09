package server

import Message
import java.io.OutputStream
import java.net.Socket
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


class ClientHandler(private val client: Socket, private val otherClients: Set<ClientHandler>) {

    companion object {
        fun write(message: Message, writer: OutputStream) {
            writer.write((message.checkForFile()).toByteArray().size shr 24)
            writer.write((message.checkForFile()).toByteArray().size shr 16)
            writer.write((message.checkForFile()).toByteArray().size shr 8)
            writer.write((message.checkForFile()).toByteArray().size)
            writer.write((message.checkForFile()).toByteArray())
            if (message.file != null) {
                writer.write(message.file!!)
            }
        }

        fun readAll(reader: InputStream): List<String> {
            var size = 0
            for (i in 0 until 4 ) {
                size = size shl 8
                size += reader.read() and 0xff
            }
            val msgBytes = ByteArray(size)
            for (i in 0 until size) {
                msgBytes[i] = (reader.read().toByte())
            }
            return """:::""".toRegex().split(msgBytes.decodeToString())
        }

        fun readMessage(message: List<String>, reader: InputStream?): Message {
            val msg = if (message.size > 3) {
                Message(message[1], message[2],
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    message[3], message[4].toInt(), getFile(reader!!, message[4].toInt()))
            } else {
                Message(message[1], message[2], LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            }
            return msg
        }

        private fun getFile (reader: InputStream, size: Int): ByteArray {
            val fileBytes = ByteArray(size)
            for (i in 0 until size) {
                fileBytes[i] = reader.read().toByte()
            }
            return fileBytes
        }

    }

    private var reader: InputStream = client.getInputStream()
    private var running: Boolean = false
    private var nick: String = ""

    private fun writeAll (message: Message) {
        otherClients.forEach { client ->
            if (client.nick != this.nick) {
                write(message, client.client.getOutputStream())
            }
        }
    }

    fun run(stream: InputStream) {
        running = true
        reader = stream
        thread { read(stream) }
        while (running) {
            val input = readLine() ?:""
            if ("-exit-" in input) shutdown()
        }
    }

    private fun read(reader: InputStream) {
        while (running) {
            val result = readAll(reader)
            if (nick == "") nick = result[2]
            else if (result[2] == "EXIT") shutdown()
            else writeAll(readMessage(result, reader))
        }
    }

    fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }
}