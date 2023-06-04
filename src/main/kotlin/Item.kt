import org.openqa.selenium.WebElement
import java.io.File

data class Item(
    val parent: File,
    val htmlElement: WebElement,
    val title: String,

) {

    var content: String = ""
    val files: MutableList<BBFile> = mutableListOf()
    val embeddings: MutableList<Embedding> = mutableListOf()

    fun scrape() {

    }
}