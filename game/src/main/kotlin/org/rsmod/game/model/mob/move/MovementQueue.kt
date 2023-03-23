package org.rsmod.game.model.mob.move

import org.rsmod.game.map.Coordinates
import java.util.LinkedList
import java.util.Queue

public class MovementQueue(
    private val waypoints: Queue<Coordinates> = LinkedList(),
    public var lastStep: Coordinates = Coordinates.ZERO,
    public var speed: MovementSpeed = DefaultMovementSpeed,
    public var noclip: Boolean = false
) : Queue<Coordinates> by waypoints
