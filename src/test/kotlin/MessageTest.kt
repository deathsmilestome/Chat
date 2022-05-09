import org.junit.Test

import org.junit.jupiter.api.Assertions

class MessageTest {

    @Test
    fun checkForFile() {
        val test1 = Message("ivan", "hello", "2022-05-01 19:00:00").checkForFile()
        val test2 = Message("ivan", """hi file-:D:\text.txt""", "2022-05-01 19:00:00").checkForFile()
        val test3 = Message("ivan", """hi file-:D:\texxt.txt""", "2022-05-01 19:00:00").checkForFile()
        val test4 = Message("ivan", """hi file-:D:\texxt.txt file-:asdasd""", "2022-05-01 19:00:00").checkForFile()
        Assertions.assertEquals("2022-05-01 19:00:00:::ivan:::hello", test1)
        Assertions.assertEquals("2022-05-01 19:00:00:::ivan:::hi file-:D:\\text.txt:::text.txt:::3", test2)
        Assertions.assertEquals("2022-05-01 19:00:00:::ivan:::invalid path", test3)
        Assertions.assertEquals("2022-05-01 19:00:00:::ivan:::", test4)
    }
}