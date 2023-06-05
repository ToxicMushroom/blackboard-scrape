import StringUtils.htmlToMarkdown
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.io.File

class LinkItem(parent: File, htmlElement: WebElement, title: String, val link: String) :
    Item(parent, htmlElement, title) {

    override fun scrape() {
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

    var content: String = ""
    val files: MutableList<BBFile> = mutableListOf()
    val embeddings: MutableList<Embedding> = mutableListOf()

    open fun scrape() {
        val details = htmlElement.findElementOrNull(By.className("vtbegenerated"))
        val message = details?.getAttribute("innerHTML")
        if (message != null) {
            val markdown = htmlToMarkdown(message)
            content = markdown
            println(markdown)

            val linkFile = File(parent.absolutePath + "/" + title + ".md")
            linkFile.createNewFile()
            linkFile.writeText(markdown)
        }
    }
}