package io.github.hristogochev.vortex.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
public fun randomUuid(): String = Uuid.random().toString()
