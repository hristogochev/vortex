import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Locale

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinParcelize)
}


kotlin {

    // Android
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=io.github.hristogochev.vortex.sample.util.Parcelize"
            )
        }
    }

    // Desktop
    jvm("desktop")

    // iOS
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // Wasm
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    // JS
    js(IR) {
        browser()
        binaries.executable()
    }

    // Native Macos experimental
    val macOsConfiguration: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                )
            }
        }
    }
    macosX64(macOsConfiguration)
    macosArm64(macOsConfiguration)

    sourceSets {

        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.materialIconsExtended)

            implementation(libs.kodein)
            implementation(libs.koin.compose)
            implementation(libs.koin.core)

            implementation(libs.lifecycle.kmp)

            implementation(project(":vortex"))
            implementation(project(":vortex-koin"))
            implementation(project(":vortex-kodein"))
        }

        androidMain.dependencies {
            implementation(libs.compose.activity)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)

            implementation(libs.koin.android)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "io.github.hristogochev.vortex.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.hristogochev.vortex.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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


// Native Macos experimental
compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosX64"), kotlin.targets.getByName("macosArm64"))
    distributions {
        targetFormats(Dmg)
        packageName = "VortexSample"
        packageVersion = "1.0.0"
    }
}


afterEvaluate {
    val baseTask = "createDistributableNative"
    val architectures = listOf("macosX64", "macosArm64")
    val buildTypes = listOf("debug", "release")

    architectures.forEach { architecture ->
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
}

private fun String.capitalize(): String {
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

