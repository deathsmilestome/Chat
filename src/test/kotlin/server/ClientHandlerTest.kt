package server

import Message
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ClientHandlerTest {
    @Test
    fun testReadMessage() {
        val result1 = ClientHandler.readMessage(listOf( "2022-05-01 19:00:00", "ivan", "hi"),null)
        val result2 = ClientHandler.readMessage(listOf("2022-05-01 19:00:00", "ivan", "hi", "text.txt", "3"), Files.readAllBytes(Paths.get("""D:\text.txt""")).inputStream())

        assertEquals(Message("ivan", "hi", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).checkForFile(), result1.checkForFile())
        assertEquals(Files.readAllBytes(Paths.get("""D:\text.txt""")).size, result2.file!!.size )
    }

    private fun makeStream (str : String): ByteArray {
        val listB = mutableListOf<Byte>()
        listB.add((str.toByteArray().size shr 24).toByte())
        listB.add((str.toByteArray().size shr 16).toByte())
        listB.add((str.toByteArray().size shr 8).toByte())
        listB.add(str.toByteArray().size.toByte())
        listB.addAll(str.toByteArray().toCollection(mutableListOf()))

        val br = ByteArray(listB.size)
        for (i in 0 until listB.size) {
            br[i] = listB[i]
        }
        return br
    }

    @Test
    fun testReadAll() {

        assertEquals(listOf<String>( "2022-05-01 19:00:00", "ivan", "hi"), ClientHandler.readAll(makeStream("2022-05-01 19:00:00:::ivan:::hi").inputStream()))
        assertEquals(listOf<String>( "2022-05-01 19:00:00", "ivan", "hi", "text.txt", "116"), ClientHandler.readAll(makeStream("2022-05-01 19:00:00:::ivan:::hi:::text.txt:::116").inputStream()))
    }

    @Test
    fun testWrite() {
        val outStream = ByteArrayOutputStream(300)
        val outStream1 = ByteArrayOutputStream(500)
        ClientHandler.write(Message("ivan", "hi", "2022-05-01 19:00:00" ), outStream)
        ClientHandler.write(Message("ivan", "hi", "2022-05-01 19:00:00", "text.txt", 116, Files.readAllBytes(Paths.get("""D:\text.txt"""))), outStream1)

        assertEquals(makeStream("2022-05-01 19:00:00:::ivan:::hi").decodeToString(), outStream.toByteArray().decodeToString())
        assertEquals(makeStream("2022-05-01 19:00:00:::ivan:::hi123").decodeToString().replace(""".*2022-05-01 19:00:00:::ivan:::hi""".toRegex(), ""), outStream1.toByteArray().decodeToString().replace(""".*2022-05-01 19:00:00:::ivan:::hi""".toRegex(), ""))
    }
}