package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.random.CoreRandom
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.util.ShuffledPlayerList

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
