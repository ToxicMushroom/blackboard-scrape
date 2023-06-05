import io.github.furstenheim.CodeBlockStyle
import io.github.furstenheim.CopyDown
import io.github.furstenheim.OptionsBuilder

object StringUtils {
    private val htmlConverter: CopyDown = OptionsBuilder.anOptions().run {
        withCodeBlockStyle(CodeBlockStyle.FENCED)
        withBulletListMaker("• ")
    }.let { CopyDown(it.build()) }

    fun htmlToMarkdown(html: String): String = htmlConverter.convert(html)

}