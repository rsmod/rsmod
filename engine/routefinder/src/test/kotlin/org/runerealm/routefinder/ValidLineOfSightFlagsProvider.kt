package org.runerealm.routefinder

import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations
import org.runerealm.routefinder.flag.CollisionFlag.BLOCK_WALK
import org.runerealm.routefinder.flag.CollisionFlag.GROUND_DECOR

object ValidLineOfSightFlagsProvider : ArgumentsProvider {
    private val VALID_FLAGS = intArrayOf(0, BLOCK_WALK, GROUND_DECOR, BLOCK_WALK or GROUND_DECOR)

    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext,
    ): Stream<out Arguments> {
        val arguments =
            VALID_FLAGS.flatMap { flag -> Direction.values.map { dir -> Arguments.of(dir, flag) } }
        return Stream.of(*arguments.toTypedArray())
    }
}
