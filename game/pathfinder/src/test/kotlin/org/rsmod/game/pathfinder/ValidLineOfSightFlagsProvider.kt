package org.rsmod.game.pathfinder

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.rsmod.game.pathfinder.flag.CollisionFlag.FLOOR
import org.rsmod.game.pathfinder.flag.CollisionFlag.FLOOR_DECORATION
import java.util.stream.Stream

object ValidLineOfSightFlagsProvider : ArgumentsProvider {

    private val VALID_FLAGS = intArrayOf(0, FLOOR, FLOOR_DECORATION, FLOOR or FLOOR_DECORATION)

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        val arguments = VALID_FLAGS
            .flatMap { flag -> Direction.values.map { dir -> Arguments.of(dir, flag) } }
        return Stream.of(*arguments.toTypedArray())
    }
}
