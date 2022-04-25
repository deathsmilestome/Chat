import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Message(val nick: String, val text: String, val date: String, val fileName: String? = null, val fileSize: Int? = null, val file: ByteArray? = null) {

    private fun getWithFile(path: String): Message {
        text
        val fileName = File(path).name
        val fileContent = Files.readAllBytes(Paths.get(path))
        val fileSize = fileContent.size
        println(fileContent.toString())
        return Message(nick, text, date, fileName, fileSize, fileContent)
    }
    fun checkForFile(): String {
        val messageSplited = text.split("file-:")
        if (messageSplited.size > 1) {
            if (File(messageSplited[1].trim()).exists()) {
                return getWithFile(messageSplited[1].trim()).transferWithFile()
            }
            else return Message(nick, "error", date).transfer()
        }
        else return Message(nick, text, date).transfer()
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
                fileSize + ":::" +
                file.toString()
                    .replace(":", "\\:")
    }

}