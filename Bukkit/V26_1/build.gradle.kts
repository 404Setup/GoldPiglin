dependencies {
    compileOnly("org.spigotmc:spigot:26.1.2-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
    compileOnly(project(":Bukkit"))
}

val targetJavaVersion = 25

java {
    disableAutoTargetJvm()
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}