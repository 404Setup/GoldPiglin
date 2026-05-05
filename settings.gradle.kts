pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "GoldPiglin"

include("Paper")
include(":Paper:V1_20_R1")
include(":Paper:V1_20_R4")
include(":Paper:V1_21_R2")
include(":Paper:V26_1")
include(":Bukkit")
include(":Bukkit:V1_20_R1")
include(":Bukkit:V1_20_R2")
include(":Bukkit:V1_20_R3")
include(":Bukkit:V1_20_R4")
include(":Bukkit:V1_21_R1")
include(":Bukkit:V1_21_R2")
include(":Bukkit:V1_21_R3")
include(":Bukkit:V1_21_R4")
include(":Bukkit:V1_21_R5")
include(":Bukkit:V1_21_R6")
include(":Bukkit:V1_21_R7")
include(":Bukkit:V26_1")
include("Common")
