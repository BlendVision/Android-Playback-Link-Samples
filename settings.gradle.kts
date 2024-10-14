import java.util.Properties

pluginManagement {
    repositories {
        includeBuild("build-logic")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //add below
        maven {
            url = uri("https://maven.pkg.github.com/blendvision/Android-Packages")
            credentials {
                val properties = getLocalProperties()
                username = properties.getProperty("github.packages.user")
                password = properties.getProperty("github.packages.password")
            }
        }
    }
}

fun getLocalProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = File(rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties
}

rootProject.name = "Android-Playback-Link-Sample"
include(":sample")
 