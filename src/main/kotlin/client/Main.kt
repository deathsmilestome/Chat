package client

fun main(args: Array<String>) {
    val address = "localhost"
    val port = 8080
    val client = Client(address, port)
    client.run()
}

