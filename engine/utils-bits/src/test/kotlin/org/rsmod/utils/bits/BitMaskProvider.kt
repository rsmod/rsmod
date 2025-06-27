package org.rsmod.utils.bits

import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations

object BitMaskProvider : ArgumentsProvider {
    // 32nd bit should be tested separately due to integer overflow on max expected value.
    private val i32BitValues =
        intArrayOf(
            1,
            3,
            7,
            15,
            31,
            63,
            127,
            255,
            511,
            1023,
            2047,
            4095,
            8191,
            16383,
            32767,
            65535,
            131071,
            262143,
            524287,
            1048575,
            2097151,
            4194303,
            8388607,
            16777215,
            33554431,
            67108863,
            134217727,
            268435455,
            536870911,
            1073741823,
            2147483647,
        )

    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext,
    ): Stream<out Arguments> {
        val combinations =
            i32BitValues.mapIndexed { index, maxValue -> Arguments.of(0..index, maxValue) }
        return Stream.of(*combinations.toTypedArray())
    }
}
