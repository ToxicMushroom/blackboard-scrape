import org.openqa.selenium.By
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

data class Course(
    val id: String,
    val name: String,
    val link: String,
    val starred: Boolean,
    val semester: Int
) {

    suspend fun scrape() {
        val driver = Session.driver
        driver.get(link)

        val menu = WebDriverWait(driver, Duration.ofSeconds(10))
            .until { it.findElement(By.id("courseMenuPalette_contents")) }
        val els = menu.findElements(By.cssSelector("li"))
        println(els)
    }
}