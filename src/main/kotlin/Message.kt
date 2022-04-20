class Message(val nick: String, val text: String, val date: String) {

    fun transfer(): String {
        return "date: " + date + " , " +
                "nick: " + nick + " , " +
                "text: " + text
            .replace(",", "\\,")
            .replace(":", "\\:")
    }
}