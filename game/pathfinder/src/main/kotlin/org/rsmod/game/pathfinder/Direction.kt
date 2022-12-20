package org.rsmod.game.pathfinder

internal sealed class Direction(val offX: Int, val offY: Int)

internal object South : Direction(0, -1)
internal object North : Direction(0, 1)
internal object West : Direction(-1, 0)
internal object East : Direction(1, 0)
internal object NorthEast : Direction(1, 1)
internal object SouthEast : Direction(1, -1)
internal object NorthWest : Direction(-1, 1)
internal object SouthWest : Direction(-1, -1)
