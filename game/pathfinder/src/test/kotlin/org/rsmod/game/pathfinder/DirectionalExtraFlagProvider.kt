package org.rsmod.game.pathfinder

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NPCS
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_PLAYERS
import java.util.stream.Stream

object DirectionalExtraFlagProvider : ArgumentsProvider {

    private val EXTRA_FLAGS = intArrayOf(BLOCK_PLAYERS, BLOCK_NPCS, BLOCK_PLAYERS or BLOCK_NPCS)

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        val arguments = EXTRA_FLAGS
            .flatMap { flag -> Direction.values.map { dir -> Arguments.of(dir, flag) } }
        return Stream.of(*arguments.toTypedArray())
    }
}
