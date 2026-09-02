rootProject.name = "VortexSample"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":shared")

includeBuild("..") {
    dependencySubstitution {
        substitute(module("io.github.hristogochev:vortex")).using(project(":vortex"))
        substitute(module("io.github.hristogochev:vortex-koin")).using(project(":vortex-koin"))
        substitute(module("io.github.hristogochev:vortex-kodein")).using(project(":vortex-kodein"))
    }
}