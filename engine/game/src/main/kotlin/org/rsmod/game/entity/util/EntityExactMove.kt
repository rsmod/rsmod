package org.rsmod.game.entity.util

public data class EntityExactMove(
    val deltaX1: Int,
    val deltaZ1: Int,
    val deltaX2: Int,
    val deltaZ2: Int,
    val clientDelay1: Int,
    val clientDelay2: Int,
    val direction: Int,
)
