import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    idea
    `java-library`
    kotlin("jvm") version "2.3.0"
}

val projectVersion: String by project
val projectMavenGroup: String by project
val projectId: String by project

version = projectVersion
group = projectMavenGroup

tasks.wrapper {
    gradleVersion = "9.2.1"
    distributionSha256Sum = "f86344275d1b194688dd330abf9f6f2344cd02872ffee035f2d1ea2fd60cf7f3"
    distributionType = Wrapper.DistributionType.ALL
}

base.archivesName = projectId

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_25
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks {
    withType<JavaCompile> {
        options.release = 25
    }

    jar {
        inputs.property("archivesName", project.base.archivesName)
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
