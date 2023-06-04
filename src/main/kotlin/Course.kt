import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration

data class Course(
    val parent: File,
    val id: String,
    val name: String,
    val link: String,
    val starred: Boolean,
    val semester: Int
) {

    val logger by Log
    val folders: MutableList<Folder> = mutableListOf()

    suspend fun scrape() {
        val driver = Session.driver
        driver.get(link)

        val menu = WebDriverWait(driver, Duration.ofSeconds(10))
            .until { it.findElement(By.id("courseMenuPalette_contents")) }
        val els = menu.findElements(By.cssSelector("li > a"))

        logger.info("Found ${els.size} elements in menu of $name")

        els.forEach { link ->
            val span = link.findElementOrNull(By.tagName("span")) ?: return@forEach
            val folderName = span.getAttribute("title")
            if (Session.ignoredFolders.contains(folderName)) return@forEach

            logger.info(folderName)
            val path = parent.absolutePath + "/" + folderName
            val file = File(path)
            if (file.mkdir()) println("Created $path")

            val folder = Folder(file, folderName, link.getAttribute("href"))
            folders.add(folder)
        }

        folders.forEach {
            it.scrape()
        }

        logger.info(folders.joinToString())
    }
}

fun WebElement.findElementOrNull(cssSelector: By): WebElement? {
    return try {
        findElement(cssSelector)
    } catch (e: NoSuchElementException) {
        null
    }
}
