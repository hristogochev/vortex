package io.github.hristogochev.vortex.sample.bottomSheetNavigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.sample.basicNavigation.BasicScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNavigation(){
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = {
                showBottomSheet = true
            }
        ) {
            Text(text = "Show BottomSheet")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
        ) {
            Navigator(BasicScreen(0, wrapContent = true), disposeOnForgotten = true)
        }
    }
}