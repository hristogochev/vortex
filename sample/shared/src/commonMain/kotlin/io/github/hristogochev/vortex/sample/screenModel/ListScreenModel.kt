package io.github.hristogochev.vortex.sample.screenModel

import io.github.hristogochev.vortex.model.ScreenModel
import io.github.hristogochev.vortex.sample.common.getSampleItems

class ListScreenModel : ScreenModel {

    val items = getSampleItems()

    override fun onDispose() {
        println("ScreenModel: dispose list")
    }
}
