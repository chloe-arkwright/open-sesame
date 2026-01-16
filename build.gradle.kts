import kotlin.reflect.KProperty

plugins {
    idea
    `java-library`
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.modmuss.publish)
}

class NonNullPropertyDelegate(val properties: ExtraPropertiesExtension = project.extra): MutablePropertyDelegate {
    override fun <T> setValue(receiver: Any?, property: KProperty<*>, value: T) {
        val name = property.name.replace(Regex("([A-Z])")) { ".${it.value.lowercase()}" }

        properties.set(name, value)
    }

    override fun <T> getValue(receiver: Any?, property: KProperty<*>): T {
        val name = property.name.replace(Regex("([A-Z])")) { ".${it.value.lowercase()}" }

        return properties.get(name) as T
    }
}; val props = NonNullPropertyDelegate()

val projectVersion: String by props
val projectGroup: String by props
val projectId: String by props

version = projectVersion
group = projectGroup

tasks.wrapper {
    gradleVersion = "9.2.1"
    distributionSha256Sum = "f86344275d1b194688dd330abf9f6f2344cd02872ffee035f2d1ea2fd60cf7f3"
    distributionType = Wrapper.DistributionType.ALL
}

base.archivesName = projectId

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
}

tasks {
    withType<JavaCompile> {
        options.release = 21
    }

    jar {
        inputs.property("archivesName", project.base.archivesName)
        version = "${projectVersion}+${libs.versions.minecraft.get()}"

        from("LICENSE")
    }

    processResources {
        inputs.property("project", mapOf(
            "version" to projectVersion
        ))

        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
