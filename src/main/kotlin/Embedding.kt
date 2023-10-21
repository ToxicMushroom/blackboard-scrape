import java.io.File
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Date

class Image(parent: File, name: String, link: String) : Embedding(parent, name) {
    override fun scrape() {
        TODO("Not yet implemented")
    }
}

class KalturaVideo(
    private val parent: File,
    title: String,
    private val partnerId: String,
    private val entryId: String
) : Embedding(parent, "") {

    private lateinit var createdAt: TemporalAccessor

    init {
        this.name = parseTitle(title)
    }

    private fun parseTitle(title: String): String {
        val dateTimeRegex = Regex("\\d+-\\d+-\\d+ \\d+:\\d+")
        val dateRegex = Regex("\\d+-\\d+-\\d+")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val normalizedTitle = URLDecoder.decode(title, "UTF-8")
        val matchRange = dateTimeRegex.find(normalizedTitle)?.groups?.get(0)?.range
            ?: dateRegex.find(normalizedTitle)?.groups?.get(0)?.range

        val dateTimeString = matchRange?.let { normalizedTitle.substring(it) } ?: "1970-01-01 00:00"
        createdAt = try {
            dateTimeFormatter.parse(dateTimeString)
        } catch (t: Exception) {
            dateFormatter.parse(dateTimeString)
        }
        val nameEnd = matchRange?.first ?: normalizedTitle.withIndex().findLast { it.value == '(' }!!.index
        return normalizedTitle.take(nameEnd - 1).trim(' ', ',')
    }

    override fun scrape() {
        val process = ProcessBuilder("yt-dlp -f b kaltura:$partnerId:$entryId".split(" "))
            .directory(parent)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
        val res = process.start()
        res.waitFor()
        logger.info { "Downloaded video $name in $parent" }
    }

}


abstract class Embedding(parent: File, open var name: String) {
    val logger = logger()
    abstract fun scrape()
}