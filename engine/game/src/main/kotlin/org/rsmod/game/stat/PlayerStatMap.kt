package org.rsmod.game.stat

import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap
import org.rsmod.annotations.InternalApi
import org.rsmod.game.type.stat.StatType

public class PlayerStatMap(
    private val xp: Byte2IntOpenHashMap = Byte2IntOpenHashMap(),
    private val baseLevels: Byte2ByteOpenHashMap = Byte2ByteOpenHashMap(),
    private val currLevels: Byte2ByteOpenHashMap = Byte2ByteOpenHashMap(),
) {
    public fun getXP(stat: StatType): Int = getFineXP(stat) / XP_FINE_PRECISION

    public fun setXP(stat: StatType, xp: Int) {
        setFineXP(stat, xp * XP_FINE_PRECISION)
    }

    public fun getFineXP(stat: StatType): Int = xp.getOrDefault(stat.id.toByte(), 0)

    public fun setFineXP(stat: StatType, xp: Int) {
        require(xp in 0..MAX_FINE_XP) {
            "`xp` must be within range [0..$MAX_FINE_XP]. (stat=$stat, xp=$xp)"
        }
        this.xp[stat.id.toByte()] = xp
    }

    @InternalApi
    public fun getBaseLevel(stat: StatType): Byte = baseLevels.getOrDefault(stat.id.toByte(), 1)

    public fun setBaseLevel(stat: StatType, level: Byte) {
        this.baseLevels[stat.id.toByte()] = level
    }

    @InternalApi
    public fun getCurrentLevel(stat: StatType): Byte = currLevels.getOrDefault(stat.id.toByte(), 1)

    public fun setCurrentLevel(stat: StatType, level: Byte) {
        this.currLevels[stat.id.toByte()] = level
    }

    public companion object {
        public const val MAX_XP: Int = 200_000_000
        public const val XP_FINE_PRECISION: Int = 10
        public const val MAX_FINE_XP: Int = MAX_XP * XP_FINE_PRECISION

        public fun toFineXP(xp: Double): Double = xp * XP_FINE_PRECISION

        public fun normalizeFineXP(fineXp: Int): Int = fineXp / XP_FINE_PRECISION
    }
}
