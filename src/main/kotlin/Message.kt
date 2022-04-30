import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Message(
    private val nick: String,
    private val text: String,
    private val date: String,
    private val fileName: String? = null,
    private val fileSize: Int? = null,
    var file: ByteArray? = null) {

    private fun getWithFile(path: String): Message {
        text
        val fileName = File(path).name
        val fileContent = Files.readAllBytes(Paths.get(path))
        this.file = fileContent
        val fileSize = fileContent.size
        return Message(nick, text, date, fileName, fileSize, fileContent)
    }

    fun checkForFile(): String {
        val messageSpited = text.split("file-:")
        return if (messageSpited.size > 1) {
            if (File(messageSpited[1].trim()).exists()) {
                getWithFile(messageSpited[1].trim()).transferWithFile()
            } else Message(nick, "error", date).transfer()
        } else Message(nick, text, date).transfer()
    }

    private fun transfer(): String {
        return  date + ":::" +
                nick + ":::" +
                text
                    .replace(":", "\\:")
    }

    private fun transferWithFile(): String {
        return  date + ":::" +
                nick + ":::" +
                text + ":::" +
                fileName + ":::" +
                fileSize.toString()
                    .replace(":", "\\:")
    }
}