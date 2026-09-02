package io.github.hristogochev.vortex.sample.screenModel

import io.github.hristogochev.vortex.model.ScreenModel
import io.github.hristogochev.vortex.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsScreenModel(
    val index: Int,
) : ScreenModel {

    private val mutableState = MutableStateFlow<State>(State.Loading)
    val state = mutableState.asStateFlow()

    sealed class State {
        data object Loading : State()
        data class Result(val item: String) : State()
    }

    fun getItem(index: Int) {
        screenModelScope.launch {
            delay(1_000)
            mutableState.value = State.Result("Item #$index")
        }
    }

    override fun onDispose() {
        println("ScreenModel: dispose details")
    }
}
