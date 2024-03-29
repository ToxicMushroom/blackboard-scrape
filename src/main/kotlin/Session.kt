import io.github.cdimascio.dotenv.DotenvBuilder
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import org.openqa.selenium.Cookie
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import java.io.File

object Session {
    fun initCookie(cookie: String) {
        cookie
            .split("; ")
            .map { it.split("=") }
            .forEach { (name, value) ->
                println("Added Cookie: $name - $value")
                driver.manage().addCookie(Cookie(name, value))
            }

    }
    val env = DotenvBuilder()
        .ignoreIfMissing()
        .load()

    val bbHost: String = env["BB_HOST"]
    val ignoredFolders: Set<String> = setOf("Contacts", "Collaborate", "ECTS", "Kalender", "Agenda")
    val cookie = File("./cookie").readText()
    val bbCookie = File("./bb_cookie").readText()
    val driver = FirefoxDriver(FirefoxOptions())

    val httpClient = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
        val config = this
        defaultRequest {
            this.url("https://toledo.kuleuven.be/portal/#/home")
            this.headers {
                this["Cookie"] = bbCookie
            }
        }
    }
}