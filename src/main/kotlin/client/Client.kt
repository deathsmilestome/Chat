package client

import Message
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class Client(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true
    private var nick: String = ""

    init {
        println("Connected to server at $address on port $port")
    }

    private val reader: Scanner = Scanner(connection.getInputStream())
    private val writer: OutputStream = connection.getOutputStream()

    fun run() {
        thread { read() }
        println("Nick: ")
        nick = readLine() ?: ""
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
                val answer = Message(nick, input, Calendar.getInstance().time.toString()).transfer()
                write(answer)
            }
        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun read() {
        while (connected)
            println(reader.nextLine())
    }
}