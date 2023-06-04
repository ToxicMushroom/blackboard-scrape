import java.io.File

suspend fun main(args: Array<String>) {
    println("Hello World!")
    val file = File("./scraped")
    if (!file.mkdir() && !file.exists()) {
        println("Failed to create directory ./scraped")
        return
    }
    val courses = Courses(file)
    courses.scrape()

    // TODO: store on disc

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}