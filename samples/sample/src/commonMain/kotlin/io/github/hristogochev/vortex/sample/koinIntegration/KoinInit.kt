package io.github.hristogochev.vortex.sample.koinIntegration

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(app: KoinApplication.() -> Unit = {}) {
    startKoin {
        modules(sampleKoinModule)
        this.app()
    }
}