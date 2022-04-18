import server.ClientHandler
import java.net.ServerSocket
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val server = ServerSocket(8080)
    println("Server is running on port ${server.localPort}")

    while (true) {
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")
        thread { ClientHandler(client).run() }
    }
}