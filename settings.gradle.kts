pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
            content { includeGroupAndSubgroups("net.fabricmc"); includeGroup("fabric-loom") }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

buildscript {
    dependencies {
        classpath("com.google.code.gson:gson:2.13.1")
    }
}

rootProject.name = "open-sesame"
