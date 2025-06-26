package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.random.CoreRandom
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.util.ShuffledPlayerList

public class PlayerIdShuffleProcess
@Inject
constructor(
    @CoreRandom private val random: GameRandom,
    private val shuffledPlayerList: ShuffledPlayerList,
) {
    private var nextShuffle = 0

    public fun process() {
        if (--nextShuffle > 0) {
            return
        }
        nextShuffle = random.of(SHUFFLE_INTERVAL)
        shuffledPlayerList.shuffle()
    }

    private companion object {
        private val SHUFFLE_INTERVAL = 100..150
    }
}
