package server

import java.net.ServerSocket
import kotlin.concurrent.thread

fun main() {
    val server = ServerSocket(8080)
    println("Server is running on port ${server.localPort}")
    val clients = mutableSetOf<ClientHandler>()
    while (true) {
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")
        val clientHandler = ClientHandler(client, clients)
        clients.add(clientHandler)
        thread { clientHandler.run(client.getInputStream()) }
    }
}
