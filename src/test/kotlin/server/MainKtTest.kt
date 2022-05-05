package server

import org.junit.Test

import org.junit.Assert.*
import kotlin.concurrent.thread

class MainKtTest {
        @Test
        fun main() {
            val server = thread { server.main()}
            val client = thread { client.main() }
            assertTrue(server.isAlive)
            assertTrue(client.isAlive)
            server.interrupt()
            client.interrupt()
        }
    }