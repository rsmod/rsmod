package org.rsmod.routefinder

import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations
import org.rsmod.routefinder.flag.CollisionFlag.WR_GRND
import org.rsmod.routefinder.flag.CollisionFlag.WR_GROUND_DECOR

object ValidLineOfSightFlagsProvider : ArgumentsProvider {
    private val VALID_FLAGS = intArrayOf(0, WR_GRND, WR_GROUND_DECOR, WR_GRND or WR_GROUND_DECOR)

    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext,
    ): Stream<out Arguments> {
        val arguments =
            VALID_FLAGS.flatMap { flag -> Direction.values.map { dir -> Arguments.of(dir, flag) } }
        return Stream.of(*arguments.toTypedArray())
    }
}
