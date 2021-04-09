package org.rsmod.plugins.api.model

import org.rsmod.game.model.domain.Direction

val Direction.angle: Int
    get() = when (this) {
        Direction.South -> 0
        Direction.SouthWest -> 256
        Direction.East -> 512
        Direction.NorthWest -> 768
        Direction.North -> 1024
        Direction.NorthEast -> 1280
        Direction.West -> 1536
        Direction.SouthEast -> 1792
    }
