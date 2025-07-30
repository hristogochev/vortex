
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Vortex"
include(":vortex")
include(":vortex-koin")
include(":vortex-kodein")
include(":samples:android")
include(":samples:multiplatform")
