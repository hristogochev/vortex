package io.github.hristogochev.vortex.sample.kodeinIntegration

import org.kodein.di.DI
import org.kodein.di.bindProvider

val sampleKodeInModule = DI.Module(name = "sample") {
    bindProvider { KodeinScreenModel() }
}