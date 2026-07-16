pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// highlight-maven-dependency
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "IMG.LY Artifactory"
            url = uri("https://maven.img.ly/maven")
            mavenContent {
                includeGroup("ly.img")
            }
        }
    }
}
// highlight-maven-dependency

rootProject.name = "My App"
include(":app")
