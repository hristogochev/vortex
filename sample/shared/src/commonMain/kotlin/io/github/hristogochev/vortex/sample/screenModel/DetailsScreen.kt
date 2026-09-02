package io.github.hristogochev.vortex.sample.screenModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.model.rememberScreenModel
import io.github.hristogochev.vortex.navigator.LocalNavigator
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.uniqueScreenKey
import io.github.hristogochev.vortex.util.currentOrThrow

data class DetailsScreen(
    val index: Int,
    override val key: String = uniqueScreenKey(),
) : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DetailsScreenModel(index) }
        val state by screenModel.state.collectAsState()

        when (val result = state) {
            is DetailsScreenModel.State.Loading -> LoadingContent()
            is DetailsScreenModel.State.Result -> DetailsContent(
                screenModel,
                result.item,
                navigator::pop
            )
        }

        LaunchedEffect(currentCompositeKeyHash) {
            screenModel.getItem(index)
        }
    }

    @Composable
    fun LoadingContent() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun DetailsContent(instance: Any, item: String, onClick: () -> Unit) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = item,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = instance.toString().substringAfterLast('.'),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                content = { Text(text = "Back") }
            )
        }
    }
}

