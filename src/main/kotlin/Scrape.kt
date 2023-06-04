import java.io.File

suspend fun main(args: Array<String>) {
    println("Hello World!")
    val courses = Courses(File("./"))
    courses.scrape()



    // TODO: store on disc


    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}