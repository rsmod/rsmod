package org.rsmod.game.model.step

import org.rsmod.game.model.map.Coordinates
import java.util.LinkedList
import java.util.Queue

sealed class StepSpeed {
    object Walk : StepSpeed()
    object Run : StepSpeed()
}

class StepQueue internal constructor(
    private val steps: Queue<Coordinates> = LinkedList()
) : Queue<Coordinates> by steps
