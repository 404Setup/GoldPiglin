plugins {
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
}