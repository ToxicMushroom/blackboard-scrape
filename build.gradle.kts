import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
}

application.mainClass.set("me.melijn.ScrapeKt")
group = "me.melijn.blackboard"
version = "0.0.1-SNAPSHOT"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven("https://reposilite.melijn.com/snapshots")
    maven("https://reposilite.melijn.com/shitpack")

    maven("https://duncte123.jfrog.io/artifactory/maven")

    // pooppack mirror
    maven("https://nexus.melijn.com/repository/jitpack/")
}

val jackson = "2.14.2" // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core

val ktor = "2.2.4"   // https://mvnrepository.com/artifact/io.ktor/ktor-client-cio
val apollo = "3.8.0" // https://mvnrepository.com/artifact/com.apollographql.apollo3/apollo-runtime
val kotlinX = "1.6.4" // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
val kotlin = "1.8.20"
val scrimage = "4.0.34"

val jda = "5.0.0-beta.6"
val kordEx = "1.5.7-SNAPSHOT"
val kordKommons = "0.0.6-SNAPSHOT"

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
    implementation(kotlin("stdlib-jdk8"))

    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-firefox-driver
    implementation("org.seleniumhq.selenium:selenium-java:4.9.1")
    implementation("org.seleniumhq.selenium:selenium-firefox-driver:4.9.1")

    // expiring map, https://search.maven.org/artifact/net.jodah/expiringmap
    implementation("net.jodah:expiringmap:0.5.10")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinX")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinX")

    // https://search.maven.org/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-jdk8
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinX")

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.4.6")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation("com.fasterxml.jackson.core:jackson-core:$jackson")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jackson")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // https://mvnrepository.com/artifact/io.ktor/ktor-client-cio
    implementation("io.ktor:ktor:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
    implementation("io.ktor:ktor-client-logging:$ktor")

    // Ktor Client
    implementation("io.ktor:ktor-client-okhttp:$ktor")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")

    // Ktor Server
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor")

    // https://github.com/cdimascio/dotenv-kotlin
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.2.4")

    implementation("io.github.microutils:kotlin-logging:2.1.23")

    testImplementation(kotlin("test"))
}

tasks {
    withType(JavaCompile::class) {
        options.encoding = "UTF-8"
    }
    withType(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
                "-Xskip-prerelease-check"
            )
        }
    }

    shadowJar {
        isZip64 = true
        mergeServiceFiles()
        archiveFileName.set("bb-scrape.jar")
    }

    test {
        useJUnitPlatform()
    }
}
