package org.rsmod.game.entity.util

import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

public class ShuffledPlayerList(private val playerList: PlayerList) : Iterable<Player> {
    private val shuffledSlots = IntArray(playerList.capacity) { it }

    public fun shuffle() {
        shuffledSlots.shuffle()
    }

    override fun iterator(): Iterator<Player> = ShuffledListIterator()

    private inner class ShuffledListIterator : Iterator<Player> {
        private var cursor = 0

        override fun hasNext(): Boolean {
            while (cursor < shuffledSlots.size) {
                val slot = shuffledSlots[cursor]
                val entry = playerList[slot]
                if (entry != null) {
                    return true
                }
                cursor++
            }
            return false
        }

        override fun next(): Player {
            val slot = shuffledSlots[cursor++]
            val entry = playerList[slot]
            return checkNotNull(entry)
        }
    }
}
