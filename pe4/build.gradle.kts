plugins { base }

subprojects {
    apply(plugin = "java-library")
    group = "org.example"
    version = "1.0-SNAPSHOT"
    repositories { mavenCentral() }
    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(27))
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    dependencies {
        "testImplementation"(platform("org.junit:junit-bom:5.11.4"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
    tasks.withType<Test>().configureEach { useJUnitPlatform() }
}
tasks.named("build") { dependsOn(subprojects.map { "${it.path}:build" }) }
tasks.register("test") { dependsOn(subprojects.map { "${it.path}:test" }) }
tasks.register("run") { dependsOn(":order-app:run") }
