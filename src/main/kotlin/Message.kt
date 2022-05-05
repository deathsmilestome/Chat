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
        val fileName = File(path).name
        val fileContent = Files.readAllBytes(Paths.get(path))
        this.file = fileContent
        val fileSize = fileContent.size
        return Message(nick, text, date, fileName, fileSize, fileContent)
    }

    fun checkForFile(): String {
        val messageSpited = text.split("file-:")
        if (messageSpited.size > 2) {
            println("You can send only one file per message")
            return Message(nick, "", date).transfer()
        }
        return if (messageSpited.size > 1) {
            val path = messageSpited[1].split(" ")[0]
            if (File(path).exists()) {
                getWithFile(path).transferWithFile()
            } else Message(nick, "invalid path", date).transfer()
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