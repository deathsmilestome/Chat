package client

fun main() {
    val address = "localhost"
    val port = 8080
    val client = Client(address, port)
    client.run()
}

