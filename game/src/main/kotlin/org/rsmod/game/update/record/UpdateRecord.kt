package org.rsmod.game.update.record

/**
 * A mutable data class for keeping track of an individual
 * player within the world.
 *
 * The reason for this class being mutable is that it would
 * otherwise be constructed exponentially throughout player
 * updating. In any other case, this class would be immutable.
 */
data class UpdateRecord(
    val index: Int,
    var flag: Int = 0,
    var local: Boolean = false,
    var coordinates: Int = 0,
    var reset: Boolean = false
)
