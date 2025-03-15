plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.15"
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
}