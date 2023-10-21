import Session.httpClient
import StringUtils.htmlToMarkdown
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.io.File

class LinkItem(parent: File, htmlElement: WebElement, title: String, val link: String) :
    Item(parent, htmlElement, title) {

    override suspend fun scrape() {
        val linkFile = File(parent.absolutePath + "/" + title + ".desktop")
        linkFile.createNewFile()
        linkFile.writeText(
            """
            [Desktop Entry]
            Encoding=UTF-8
            Type=Link
            Name=$title
            URL=$link
            Icon=text-html
            """.trimIndent()
        )
    }
}

open class Item(
    val parent: File,
    val htmlElement: WebElement,
    val title: String
) {

    val logger by Log
    var content: String = ""
    val files: MutableList<BBFile> = mutableListOf()
    val embeddings: MutableList<Embedding> = mutableListOf()
    val kalturaVidPattern = Regex("/p/(.+?)/sp/.*?/entry_id/(.*?)/version")
    val kalturaVidTitlePattern = Regex("&title=(.+?)&")

    open suspend fun scrape() {
        val details = htmlElement.findElementOrNull(By.className("vtbegenerated"))
        val attachments = htmlElement.findElementOrNull(By.className("attachments"))
        val kalturaVideo = htmlElement.findElementOrNull(By.tagName("iframe"))
        if (kalturaVideo != null) {
            val link = kalturaVideo.getAttribute("src")
            val title = kalturaVidTitlePattern.find(link)?.groupValues?.get(1)
            kalturaVidPattern.find(link)?.let { matchResult ->
                val partnerId = matchResult.groupValues[1]
                val entryId = matchResult.groupValues[2]
                embeddings.add(KalturaVideo(parent, title!!, partnerId, entryId))
            }
        }

        val message = details?.getAttribute("innerHTML")
        if (message != null) {
            val markdown = htmlToMarkdown(message)
            content = markdown
            println(markdown)

            val linkFile = File(parent.absolutePath + "/" + escape(title) + ".md")
            linkFile.createNewFile()
            linkFile.writeText(markdown)
        }
        if (attachments != null) {
            val attachmentEls = attachments.findElements(By.xpath("./li"))
            attachmentEls.forEach { attachment ->
                val attachmentText = attachment.text.trim()
                val link = attachment.findElementOrNull(By.cssSelector("a")) ?: return@forEach
                val titleHtml = link.getAttribute("innerHTML").trim()
                val title = titleHtml.takeLastWhile { it != '>' }.removePrefix("&nbsp;")
                val href = link.getAttribute("href")

                if (title.endsWith(".mp4")) {
                    logger.warn { "Skipping mp4 $title" }
                    return@forEach
                }
                val size = attachmentText.removePrefix(title).trim().removeSurrounding("(", ")")


                val file = BBFile(title, href, size)
                files.add(file)
            }
        }

        files.forEach {
            val path = parent.absolutePath + "/" + it.name
            val file = File(path)
            if (file.exists()) {
                logger.info { "Skipping $path" }
                return@forEach
            }

            val data = httpClient
                .get(it.link)
                .body<ByteArray>()
            try {
                file.createNewFile()
                file.writeBytes(data)
                logger.info { "Created $path" }
            } catch (t: Throwable) {
                logger.error { "Failed to create $path" }
            }
        }

        embeddings.forEach {
            it.scrape()
        }
    }

    private fun escape(title: String): String {
        return title.replace("/", "-")
    }
}