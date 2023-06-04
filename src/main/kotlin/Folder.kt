import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class Folder(
    val parent: File,
    val name: String,
    val link: String
) {
    val children: MutableList<Folder> = mutableListOf()
    val items: MutableList<Item> = mutableListOf()
    val logger = logger()

    suspend fun scrape() {

        val driver = Session.driver
        driver.get(link)

        val contentEl =
            WebDriverWait(driver, Duration.ofSeconds(3))
                .until { it.findElement(By.id("content")) }
        val list = contentEl.findElementOrNull(By.id("content_listContainer"))?: return

        val els = list.findElements(By.xpath("./li"))
        logger.info { "Found ${els.size} posts in $name" }
        els.forEach {
            val titleWrapEl = it.findElementOrNull(By.cssSelector("* > div.item")) ?: return@forEach
            val title = titleWrapEl.text
            val link = titleWrapEl.findElementOrNull(By.cssSelector("a"))?.getAttribute("href")
            if (link != null) {
                // Is folder
                val path = parent.absolutePath + "/" + title
                val file = File(path)
                if (file.mkdir()) println("Created $path")
                val item = Item(parent, it, title)
                items.add(item)
                children.add(Folder(file, title, link))
            } else {
                val item = Item(parent, it, title)
                items.add(item)
            }
            delay(100.milliseconds)
        }

        items.forEach {
            it.scrape()
        }

        children.forEach {
            it.scrape()
        }
        // populate children
        // scrape children and items
    }
}
