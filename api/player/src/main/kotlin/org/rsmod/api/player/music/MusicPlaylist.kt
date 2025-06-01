package org.rsmod.api.player.music

import org.rsmod.api.music.Music
import org.rsmod.api.random.DefaultGameRandom
import org.rsmod.api.random.GameRandom

@JvmInline
public value class MusicPlaylist(public val packed: Int) {
    private val position: Int
        get() = (packed shr POSITION_BIT_OFFSET) and POSITION_BIT_MASK

    private val cycleCount: Int
        get() = (packed shr CYCLE_COUNT_BIT_OFFSET) and CYCLE_COUNT_BIT_MASK

    private val shuffleSeed: Int
        get() = (packed shr SHUFFLE_SEED_BIT_OFFSET) and SHUFFLE_SEED_BIT_MASK

    public constructor(
        position: Int,
        cycleCount: Int,
        shuffleSeed: Int,
    ) : this(pack(position, cycleCount, shuffleSeed))

    public fun nextPosition(trackCount: Int): MusicPlaylist {
        val nextPosition = (position + 1) % trackCount
        val nextCycleCount = if (nextPosition == 0) cycleCount + 1 else cycleCount
        return MusicPlaylist(nextPosition, nextCycleCount, shuffleSeed)
    }

    public fun getShuffledTrack(tracks: List<Music>): Music {
        val shuffled = toShuffledList(tracks)
        return shuffled[position]
    }

    public fun toShuffledList(tracks: List<Music>): List<Music> {
        require(tracks.isNotEmpty()) { "Track list must not be empty." }
        val random = DefaultGameRandom(shuffleSeed.toLong())
        val shuffled = tracks.shuffled(random)
        if (cycleCount == 0) {
            return shuffled
        }
        val first = shuffled.first()
        val remaining = tracks.filter { it != first }
        val newRandom = DefaultGameRandom((shuffleSeed + cycleCount).toLong())
        val shuffledRemaining = remaining.shuffled(newRandom)
        return listOf(first) + shuffledRemaining
    }

    private fun <T> List<T>.shuffled(random: GameRandom): List<T> {
        val shuffled = toMutableList()
        for (i in shuffled.size - 1 downTo 1) {
            val randomIndex = random.of(i + 1)
            val temp = shuffled[i]
            shuffled[i] = shuffled[randomIndex]
            shuffled[randomIndex] = temp
        }
        return shuffled
    }

    override fun toString(): String {
        return "MusicPlaylist(position=$position, cycleCount=$cycleCount, shuffleSeed=$shuffleSeed)"
    }

    public companion object {
        private const val POSITION_BIT_COUNT = 8
        private const val CYCLE_COUNT_BIT_COUNT = 8
        private const val SHUFFLE_SEED_BIT_COUNT = 16

        private const val POSITION_BIT_OFFSET = 0
        private const val CYCLE_COUNT_BIT_OFFSET = POSITION_BIT_OFFSET + POSITION_BIT_COUNT
        private const val SHUFFLE_SEED_BIT_OFFSET = CYCLE_COUNT_BIT_OFFSET + CYCLE_COUNT_BIT_COUNT

        private const val POSITION_BIT_MASK = (1 shl POSITION_BIT_COUNT) - 1
        private const val CYCLE_COUNT_BIT_MASK = (1 shl CYCLE_COUNT_BIT_COUNT) - 1
        private const val SHUFFLE_SEED_BIT_MASK = (1 shl SHUFFLE_SEED_BIT_COUNT) - 1

        public fun create(random: GameRandom): MusicPlaylist {
            val shuffleSeed = random.of(0, SHUFFLE_SEED_BIT_MASK)
            return MusicPlaylist(0, 0, shuffleSeed)
        }

        private fun pack(position: Int, cycleCount: Int, shuffleSeed: Int): Int {
            require(position in 0..POSITION_BIT_MASK) {
                "`position` value must be within range [0..$POSITION_BIT_MASK]."
            }
            require(cycleCount in 0..CYCLE_COUNT_BIT_MASK) {
                "`cycleCount` value must be within range [0..$CYCLE_COUNT_BIT_MASK]."
            }
            require(shuffleSeed in 0..SHUFFLE_SEED_BIT_MASK) {
                "`shuffleSeed` value must be within range [0..$SHUFFLE_SEED_BIT_MASK]."
            }
            return ((position and POSITION_BIT_MASK) shl POSITION_BIT_OFFSET) or
                ((cycleCount and CYCLE_COUNT_BIT_MASK) shl CYCLE_COUNT_BIT_OFFSET) or
                ((shuffleSeed and SHUFFLE_SEED_BIT_MASK) shl SHUFFLE_SEED_BIT_OFFSET)
        }
    }
}
