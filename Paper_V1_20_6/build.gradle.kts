plugins {
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

dependencies {
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
}