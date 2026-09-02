package io.github.hristogochev.vortex.sample.common

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun getSampleItems(): List<String> =
    (0..99).map { "Item #$it | ${Uuid.random().toString().substringBefore('-')}" }
