import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration
import kotlin.time.Duration.Companion.seconds


class Courses(val parent: File) {

    // CourseIds
    val courseSet = mutableSetOf<Course>()
    val logger = logger()

    suspend fun scrape() {

        val driver = Session.driver
        driver.get("https://toledo.kuleuven.be/404.html")
        Session.initCookie(Session.cookie)
        driver.get("https://p.cygnus.cc.kuleuven.be/")
        Session.initCookie(Session.bbCookie)
        driver.get("https://toledo.kuleuven.be/portal/#/home")
        WebDriverWait(driver, Duration.ofSeconds(60))
            .until { it.findElement(By.className("enrollment-clickable-area")) }

        val elements = driver.findElements(By.className("enrollment-clickable-area"))
            .map { link ->
                val href = link.getAttribute("href")

                val titleSpan = link.findElement(By.className("learning-unit-title"))
                val text = titleSpan.text

                val idRegex = "\\[(.*)\\]".toRegex()
                val result = idRegex.find(text)
                val id = result!!.groups[1]!!.value
                val courseName = text.dropLast(result.groups[0]!!.range.count()).trim().removeSuffix(":").trim()

                val path = parent.absolutePath + "/" + courseName
                val courseParent = File(path)
                if (courseParent.mkdir()) println("Created $path")
                Course(courseParent, id, courseName, href, false, 1)
             }

        courseSet.addAll(elements)

        for (course in courseSet) {

            course.scrape()
            delay(1.seconds)
        }

        logger.info(courseSet.joinToString())
        driver.quit()
    }

}

