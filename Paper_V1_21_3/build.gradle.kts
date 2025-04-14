plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
    compileOnly(project(":Paper"))
}