package server

import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientHandler(private val client: Socket) {
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false

    fun run() {
        running = true
        write("Welcome to the club buddy!\n" +
                "To Exit, write: 'EXIT'.\n" +
                "Chat:\n")
        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT"){
                    shutdown()
                    continue
                }
                else {write("${Calendar.getInstance().time}|Master's answer: $text")}
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