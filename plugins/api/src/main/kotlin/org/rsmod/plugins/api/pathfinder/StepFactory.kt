package org.rsmod.plugins.api.pathfinder

import jakarta.inject.Inject
import org.rsmod.game.map.Coordinates
import org.rsmod.game.pathfinder.StepValidator
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import kotlin.math.sign

public class StepFactory @Inject constructor(flags: CollisionFlagMap) {

    private val validator: StepValidator = StepValidator(flags)

    public fun createPath(
        source: Coordinates,
        destination: Coordinates,
        size: Int = 1,
        extraFlag: Int = 0,
        collision: CollisionType
    ): List<Coordinates> {
        val coords = mutableListOf<Coordinates>()
        var curr = Coordinates(source.packed)
        for (i in 0 until 128 * 128) {
            if (curr == destination) break
            curr = validated(curr, destination, size, extraFlag, collision)
            if (curr == Coordinates.NULL) break
            coords += curr
        }
        return coords
    }

    /**
     * @return the next available step in between [source] and [destination] _without_
     * validating that said step is not blocked by any possible collision flags.
     */
    public fun unvalidated(source: Coordinates, destination: Coordinates): Coordinates {
        require(source != destination) { "`source` must not be equal to `destination`." }
        val offX = (destination.x - source.x).sign
        val offZ = (destination.z - source.z).sign
        return source.translate(offX, offZ)
    }

    /**
     * @return The next _validated_ step in between [source] and [destination].
     * [Coordinates.NULL] if no tile could be validated between the two given
     * coordinates.
     */
    public fun validated(
        source: Coordinates,
        destination: Coordinates,
        size: Int = 1,
        extraFlag: Int = 0,
        collision: CollisionType = CollisionType.Normal
    ): Coordinates {
        require(source != destination) { "`source` must not be equal to `destination`." }
        val level = source.level
        val signX = (destination.x - source.x).sign
        val signZ = (destination.z - source.z).sign

        val diagonal = validator.canTravel(
            level = level,
            x = source.x,
            z = source.z,
            offsetX = signX,
            offsetZ = signZ,
            size = size,
            extraFlag = extraFlag,
            collision = collision.strategy
        )
        if (diagonal) return source.translate(signX, signZ)

        val horizontal = signX != 0 && validator.canTravel(
            level = level,
            x = source.x,
            z = source.z,
            offsetX = signX,
            offsetZ = 0,
            size = size,
            extraFlag = extraFlag,
            collision = collision.strategy
        )
        if (horizontal) return source.translate(signX, 0)

        val vertical = signZ != 0 && validator.canTravel(
            level = level,
            x = source.x,
            z = source.z,
            offsetX = 0,
            offsetZ = signZ,
            size = size,
            extraFlag = extraFlag,
            collision = collision.strategy
        )
        if (vertical) return source.translate(0, signZ)

        return Coordinates.NULL
    }
}
