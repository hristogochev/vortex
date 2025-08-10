package io.github.hristogochev.vortex.sample

import android.app.Application
import io.github.hristogochev.vortex.sample.kodeinIntegration.initKodein
import io.github.hristogochev.vortex.sample.koinIntegration.initKoin
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application(), DIAware {

    override val di by lazy {
        initKodein {
            import(androidXModule(this@AndroidApp))
        }
    }

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApp)
        }
    }
}