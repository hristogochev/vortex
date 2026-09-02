package io.github.hristogochev.vortex.sample.kodeinIntegration

import org.kodein.di.DI

fun initKodein(app: DI.MainBuilder.() -> Unit = {}): DI {
    return DI {
        import(sampleKodeInModule)
        this.app()
    }
}