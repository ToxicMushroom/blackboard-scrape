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
        val list =
            contentEl.findElementOrNull(By.id("content_listContainer"))
                ?: contentEl.findElementOrNull(By.id("announcementList")) ?: return

        val els = list.findElements(By.xpath("./li"))
        logger.info { "Found ${els.size} posts in $name" }
        els.forEach {
            val titleWrapEl = it.findElementOrNull(By.cssSelector("* > div.item"))
                ?: it.findElementOrNull(By.cssSelector("* > h3.item")) ?: return@forEach
            val iconEl = it.findElementOrNull(By.cssSelector("* > img.item_icon"))
            val type = iconEl?.getAttribute("src")?.removePrefix(Session.bbHost)
            val title = titleWrapEl.text
            val link = titleWrapEl.findElementOrNull(By.cssSelector("a"))?.getAttribute("href")
            when (type) {
                // Is folder
                "/images/ci/sets/set01/folder_on.gif" -> {
                    val path = parent.absolutePath + "/" + title
                    val file = File(path)
                    if (file.mkdir()) println("Created $path")
                    val item = Item(parent, it, title)
                    items.add(item)
                    children.add(Folder(file, title, link!!))
                }

                // Is weblink
                "/images/ci/sets/set01/link_on.gif" -> {
                    val item = LinkItem(parent, it, title, link!!)
                    items.add(item)
                }

                // Is document/text post
                else -> {
                    val item = Item(parent, it, title)
                    items.add(item)
                }
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
