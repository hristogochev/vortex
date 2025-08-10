package io.github.hristogochev.vortex.sample.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ListContent(items: List<String>, onClick: ((Int) -> Unit)? = null) {
    LazyColumn {
        itemsIndexed(items) { index, item ->
            ListItem(
                headlineContent = { Text(text = item) },
                modifier = if (onClick == null) Modifier else Modifier.clickable { onClick(index) }
            )
        }
    }
}
