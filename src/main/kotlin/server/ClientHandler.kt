package server

import Message
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientHandler(private val client: Socket) {
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false
    private var nick: String = ""

    fun run() {
        running = true
        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT"){
                    shutdown()
                    continue
                }
                else {
                    //val answer = Message(nick, text, Calendar.getInstance().time.toString()).transfer()
                    write("Server get: $text")
                }
            } catch (ex: Exception) {
                write("Something gone wrong :(")
                shutdown()
            }
        }
    }
    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}