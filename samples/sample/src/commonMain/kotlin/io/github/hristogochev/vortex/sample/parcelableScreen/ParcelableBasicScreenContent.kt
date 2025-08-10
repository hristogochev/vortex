package io.github.hristogochev.vortex.sample.parcelableScreen

import io.github.hristogochev.vortex.sample.util.Parcelable
import io.github.hristogochev.vortex.sample.util.Parcelize

@Parcelize
data class ParcelableBasicScreenContent(
    val index: Int,
) : Parcelable