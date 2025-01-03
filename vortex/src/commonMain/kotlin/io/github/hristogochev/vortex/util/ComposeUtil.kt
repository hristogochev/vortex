package io.github.hristogochev.vortex.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal

public val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = current ?: error("CompositionLocal is null")

