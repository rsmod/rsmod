package org.rsmod.plugins.info.player

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.rsmod.plugins.info.player.PlayerInfo.Companion.DEFAULT_PLAYER_LIMIT
import java.util.stream.Stream

object PlayerInfoProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(PlayerInfo(DEFAULT_PLAYER_LIMIT)),
            Arguments.of(PlayerInfo(1024)),
            Arguments.of(PlayerInfo(512))
        )
    }
}
