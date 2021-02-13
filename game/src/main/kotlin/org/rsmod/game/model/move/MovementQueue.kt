package org.rsmod.game.model.move

import org.rsmod.game.model.map.Coordinates
import java.util.LinkedList
import java.util.Queue
import org.rsmod.game.model.domain.Direction

sealed class MovementSpeed {
    object Walk : MovementSpeed()
    object Run : MovementSpeed()
}

data class Step(
    val dest: Coordinates,
    val dir: Direction
)

class MovementQueue internal constructor(
    private val path: Queue<Coordinates> = LinkedList(),
    val nextSteps: MutableList<Step> = mutableListOf(),
    var speed: MovementSpeed? = null,
    var noclip: Boolean = false
) : Queue<Coordinates> by path
