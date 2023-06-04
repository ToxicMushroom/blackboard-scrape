import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration
import kotlin.time.Duration.Companion.seconds


class Courses(val root: File) {

    // CourseIds
    val courses = mutableSetOf<Course>()

    suspend fun scrape() {

        val driver = Session.driver
        driver.get("https://toledo.kuleuven.be/404.html")
        Session.initCookie()
        driver.get("https://toledo.kuleuven.be/portal/#/home")
        WebDriverWait(driver, Duration.ofSeconds(10))
            .until { driver: WebDriver -> driver.findElement(By.className("enrollment-clickable-area")) }
        val elements = driver.findElements(By.className("enrollment-clickable-area"))
            .map { link ->
                val href = link.getAttribute("href")
//                val semCircle = link.findElement(By.className("fa-circle-half-stroke"))
//                val semester = semCircle?.getAttribute("title")?.drop(9)?.toIntOrNull() ?: 1

                val titleSpan = link.findElement(By.className("learning-unit-title"))
                val text = titleSpan.text

                val idRegex = "\\[(.*)\\]".toRegex()
                val result = idRegex.find(text)
                val id = result!!.groups[1]!!.value
                val name = text.dropLast(result.groups[0]!!.range.count()).trim().removeSuffix(":").trim()

                Course(id, name, href, false, 1)
             }
        courses.addAll(elements)

        for (course in courses) {
            val path = root.absolutePath + File.pathSeparator + course.name
            if (File(path).createNewFile()) println("Created $path")
            course.scrape()
            delay(1.seconds)
        }

        driver.quit()
    }

}

