package server

import Message
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import client.Client

class ClientHandler(val client: Socket, val otherClients: Set<ClientHandler>) {
    private val reader: Scanner = Scanner(client.getInputStream())
    private var running: Boolean = false
    var nick: String = ""

    fun run() {
        running = true
        nick = reader.nextLine()
        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT"){
                    shutdown()
                    continue
                }
                else {
                    write(text)
                }
            }
            catch (ex: Exception) {
                write("Something gone wrong :(")
                shutdown()
            }
        }
    }
    private fun write(message: String) {
        otherClients.forEach { client ->
            if (client.nick != this.nick) {
                val writer: OutputStream = client.client.getOutputStream()
                writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
            }
        }
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }
}