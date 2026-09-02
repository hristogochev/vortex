import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import java.util.Locale

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinParcelize)
}


kotlin {

    // Android
    android {
        namespace = "io.github.hristogochev.vortex.sample"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=io.github.hristogochev.vortex.sample.util.Parcelize"
            )
        }

        androidResources {
            enable = true
        }
    }

    // iOS
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    // Desktop
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // Wasm
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
//        binaries.executable()
    }

    // JS
    js {
        browser()
//        binaries.executable()
    }

    // Native Macos experimental
    macosArm64 {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                )
            }
        }
    }

    sourceSets {

        val desktopMain = getByName("desktopMain")

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)


            implementation(libs.kodein)
            implementation(libs.koin.compose)
            implementation(libs.koin.core)

            implementation(libs.lifecycle.kmp)

            implementation("io.github.hristogochev:vortex:0.3.0")
            implementation("io.github.hristogochev:vortex-koin:0.3.0")
            implementation("io.github.hristogochev:vortex-kodein:0.3.0")
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}


compose.desktop {
    application {
        mainClass = "io.github.hristogochev.vortex.sample.MainKt"
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}


//// Native Macos experimental
compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosArm64"))
    distributions {
        targetFormats(Dmg)
        packageName = "VortexSample"
        packageVersion = "1.0.0"
    }
}


afterEvaluate {
    val baseTask = "createDistributableNative"
    val buildTypes = listOf("debug", "release")

    val architecture = "macosArm64"
    buildTypes.forEach buildTypeForEach@{ buildType ->
        val createAppTaskName = baseTask + buildType.capitalize() + architecture.capitalize()

        val createAppTask =
            tasks.findByName(createAppTaskName) as? AbstractNativeMacApplicationPackageTask?
                ?: return@buildTypeForEach

        val destinationDir = createAppTask.destinationDir.get().asFile
        val packageName = createAppTask.packageName.get()

        tasks.create("runNative${architecture.capitalize()}${buildType.capitalize()}") {
            group = createAppTask.group
            dependsOn(createAppTaskName)
            doLast {
                ProcessBuilder(
                    "open",
                    destinationDir.absolutePath + "/" + packageName + ".app"
                ).start().waitFor()
            }
        }
    }
}

private fun String.capitalize(): String {
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}


tasks.matching {
    it.name == "checkComposeUiTestConfigurationForJs" ||
            it.name == "checkComposeUiTestConfigurationForWasmJs"
}.configureEach {
    enabled = false
}



