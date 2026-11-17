plugins { application }
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
// Додаємо залежності JavaFaker без модульних описів як автоматичні модулі.
tasks.named<JavaCompile>("compileJava") {
    doFirst {
        options.compilerArgs.addAll(listOf("--module-path", classpath.asPath))
        classpath = files()
    }
}
// Застосовуємо також до задач запуску Main, які IntelliJ створює динамічно.
tasks.withType<JavaExec>().configureEach {
    modularity.inferModulePath.set(true)
    jvmArgs(application.applicationDefaultJvmArgs)
    val runtimePath = sourceSets.main.get().runtimeClasspath
    doFirst {
        classpath = files()
        // IntelliJ обчислює свій --module-path через classpath задачі.
        // Зберігаємо інші її аргументи, замінюючи лише модульний шлях.
        val modulePathProviders = jvmArgumentProviders.filter {
            "--module-path" in it.asArguments()
        }
        modulePathProviders.forEach { original ->
            jvmArgumentProviders.remove(original)
            jvmArgumentProviders.add(CommandLineArgumentProvider {
                val arguments = original.asArguments().toList()
                arguments.mapIndexed { index, value ->
                    if (index > 0 && arguments[index - 1] == "--module-path") runtimePath.asPath else value
                }
            })
        }
        // Останній аргумент враховує також налаштування, додані IntelliJ.
        jvmArgumentProviders.add(CommandLineArgumentProvider {
            listOf("--module-path", runtimePath.asPath)
        })
    }
}
tasks.named<CreateStartScripts>("startScripts") {
    doLast {
        // Інакше Gradle розміщує немодульні JAR у шляху класів, приховуючи javafaker від JPMS.
        unixScript.writeText(unixScript.readText().replace(
            "--module-path \"\$MODULE_PATH\"", "--module-path \"\$MODULE_PATH:\$CLASSPATH\""))
        windowsScript.writeText(windowsScript.readText().replace(
            "--module-path \"%MODULE_PATH%\"", "--module-path \"%MODULE_PATH%;%CLASSPATH%\""))
    }
}
