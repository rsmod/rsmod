package org.rsmod.game.update.record

data class UpdateRecord(
    val index: Int,
    var flag: Int = 0,
    var local: Boolean = false,
    var coordinates: Int = 0,
    var reset: Boolean = false
)
