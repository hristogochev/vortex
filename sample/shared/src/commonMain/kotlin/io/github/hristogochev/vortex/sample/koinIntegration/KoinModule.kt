package io.github.hristogochev.vortex.sample.koinIntegration

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val sampleKoinModule = module {
    factoryOf(::KoinScreenModel)
}