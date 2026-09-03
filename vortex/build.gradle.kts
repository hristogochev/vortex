import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict

    android {
        namespace = "io.github.hristogochev.vortex"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        optimization {
            consumerKeepRules.apply {
                publish = true
                file("consumer-rules.pro")
            }
        }
    }


    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs { browser() }

    js { browser() }

    sourceSets {
        val desktopMain = getByName("desktopMain")

        androidMain.dependencies {
            implementation(libs.compose.activity)
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.backhandler)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}



mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    if (gradle.startParameter.taskNames.any { it.contains("MavenCentral", ignoreCase = true) }) {
        signAllPublications()
    }

    coordinates(group.toString(), "vortex", version.toString())

    pom {
        name = "Vortex"
        description =
            "Stability-focused Compose Multiplatform Navigation Library, fork of Voyager"
        inceptionYear = "2024"
        url = "https://github.com/hristogochev/vortex/"
        licenses {
            license {
                name = "The MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "hristogochev"
                name = "Hristo Gochev"
                url = "https://github.com/hristogochev"
            }
        }
        scm {
            url = "https://github.com/hristogochev/vortex/"
            connection = "scm:git:git://github.com/hristogochev/vortex.git"
            developerConnection = "scm:git:ssh://git@github.com/hristogochev/vortex.git"
        }
    }
}

tasks.matching {
    it.name == "checkComposeUiTestConfigurationForJs" ||
            it.name == "checkComposeUiTestConfigurationForWasmJs"
}.configureEach {
    enabled = false
}

