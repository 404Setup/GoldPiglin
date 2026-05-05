plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    compileOnly(project(":Common"))
    compileOnly(project(":Paper"))
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