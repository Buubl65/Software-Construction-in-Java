plugins {
    application
    id("org.gradlex.extra-java-module-info") version "1.14"
}

dependencies {
    implementation(project(":order-processing"))
    implementation(project(":order-threading"))
    implementation("com.github.javafaker:javafaker:1.0.2")
}

application {
    mainModule.set("order.app")
    mainClass.set("org.example.app.Main")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")
}

// Плагін додає модульні назви старим бібліотекам JavaFaker.
extraJavaModuleInfo {
    deriveAutomaticModuleNamesFromFileNames = true
}

// Український текст у консолі, зокрема під час запуску кнопкою Run.
tasks.withType<JavaExec>().configureEach {
    jvmArgs(application.applicationDefaultJvmArgs)
}
