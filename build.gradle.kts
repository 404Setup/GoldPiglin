import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta13" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
}

group = "one.tranic"
version = "25.06.2"

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenLocal()
        maven("https://maven-central-asia.storage-download.googleapis.com/maven2/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://jitpack.io")
    }

    dependencies {
        compileOnly("org.slf4j:slf4j-api:2.0.16")
        compileOnly("one.tranic:t-thread:1.0.1")
        compileOnly("one.tranic:t-utils:1.3.0")
        //compileOnly(files("../t-utils-1.2.4.jar"))
    }
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":Common"))
    implementation(project(":Paper"))
    implementation(project(":Paper:V1_20_R1"))
    implementation(project(":Paper:V1_20_R4"))
    implementation(project(":Paper:V1_21_R2"))
    implementation(project(":Bukkit"))
    implementation(project(":Bukkit:V1_20_R1"))
    implementation(project(":Bukkit:V1_20_R2"))
    implementation(project(":Bukkit:V1_20_R3"))
    implementation(project(":Bukkit:V1_20_R4"))
    implementation(project(":Bukkit:V1_21_R1"))
    implementation(project(":Bukkit:V1_21_R2"))
    implementation(project(":Bukkit:V1_21_R3"))
    implementation(project(":Bukkit:V1_21_R4"))
    implementation(project(":Bukkit:V1_21_R5"))
    implementation("one.tranic:t-utils:1.3.0")
    //implementation(files("t-utils-1.2.4.jar"))
    //implementation(files("t-utils-1.2.4-sources.jar"))
    implementation("one.tranic:t-thread:1.0.1")
    compileOnly("org.slf4j:slf4j-api:2.0.16")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.13.2")
    compileOnly("com.saicone.rtag:rtag:1.5.10")
    compileOnly("com.saicone.rtag:rtag-item:1.5.10")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("it.unimi.dsi:fastutil:8.5.15")
}

val targetJavaVersion = 17

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

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

val libPackage = "one.tranic.goldpiglin.libs"

tasks.withType<ShadowJar> {
    relocate("one.tranic.t", "${libPackage}.tlib")

    minimize {
        exclude("META-INF/**")
        exclude("com/google/gson/**")
        exclude("com/google/errorprone/**")
        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/checkerframework/**")
        exclude("org/slf4j/**")
    }
}