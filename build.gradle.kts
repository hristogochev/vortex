plugins {
    alias(libs.plugins.kotlinMultiplatform) apply  false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.androidMultiplatformLibrary).apply(false)
}

allprojects {
    group = "io.github.hristogochev"
    version = "0.4.1"
}

