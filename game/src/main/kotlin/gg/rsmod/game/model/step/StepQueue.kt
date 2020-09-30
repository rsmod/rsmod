package gg.rsmod.game.model.step

import gg.rsmod.game.model.domain.Direction
import java.util.LinkedList
import java.util.Queue

class StepQueue internal constructor(
    private val steps: Queue<Direction> = LinkedList()
) : Queue<Direction> by steps
