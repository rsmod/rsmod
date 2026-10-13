package org.runerealm.api.area.checker

import org.runerealm.map.CoordGrid

/**
 * Wilderness boundaries and levels.
 *
 * Levels are ported from the client's `wilderness_level` proc (clientscript 384). It treats the
 * whole `0_46_55_0_0`..`3_52_67_63_63` zone (x 2944-3391, z 3520-4351) as the surface Wilderness
 * with `level = (z - 3520) / 8 + 1`. That zone's southern edge covers the tile south of the
 * Wilderness ditch (z 3520), the ditch itself (z 3521-3522) and the two tiles north of it (z
 * 3523-3524). The client only uses the proc while the server shows the `pvp_icons` overlay, and per
 * the wiki ("Wilderness": "The first two steps (squares/tiles) of the Wilderness are 'safe' zones,
 * in which players cannot attack or be attacked") the Wilderness proper starts two tiles north of
 * the ditch, at z [SURFACE_START_Z]. [level] reports `0` for that strip, so the level overlay, PvP,
 * death and teleport rules all agree.
 */
public object WildernessArea {
    /** The first surface tile row of the Wilderness proper, two tiles north of the ditch. */
    public const val SURFACE_START_Z: Int = 3525

    /** Returns the Wilderness level at [coord], or `0` outside it (including the ditch strip). */
    public fun level(coord: CoordGrid): Int {
        if (inDitchStrip(coord)) {
            return 0
        }
        return clientLevel(coord)
    }

    /**
     * Returns the level the client's `wilderness_level` proc computes for [coord]. Unlike [level],
     * this counts the ditch strip south of [SURFACE_START_Z] as level 1.
     */
    public fun clientLevel(coord: CoordGrid): Int {
        val x = coord.x
        val z = coord.z
        val mx = x shr 6
        val mz = z shr 6
        return when {
            mx in 52..54 &&
                mz in 62..64 &&
                !(mx == 53 && mz == 63 && (x and 63) in 21..42 && (z and 63) in 21..42) -> 5
            mx in 46..52 && mz in 55..67 -> (z - 55 * 64) / 8 + 1
            mx == 47 && mz == 158 -> (z - 155 * 64) / 8 - 1
            mx == 51 && mz == 159 -> 35
            mx == 53 && mz == 159 -> 35
            mx == 52 && mz == 161 -> 40
            mx == 27 && mz == 180 -> 21
            mx == 29 && mz == 180 -> 21
            mx == 25 && mz == 180 -> 29
            mx == 52 && mz == 160 -> 33 + ((z and 63) - 6) * 7 / 50
            mx in 46..53 && mz in 155..169 -> (z - 155 * 64) / 8 + 1
            else -> 0
        }
    }

    /**
     * Whether [coord] is inside the Wilderness. Ferox Enclave lies within the Wilderness level
     * boundaries but is a safe zone, so it does not count.
     */
    public fun contains(coord: CoordGrid): Boolean = level(coord) > 0 && !inFeroxEnclave(coord)

    /** The ditch and the tiles either side of it that the client proc counts as level 1. */
    private fun inDitchStrip(coord: CoordGrid): Boolean =
        coord.x in SURFACE_X && coord.z in SURFACE_ZONE_START_Z until SURFACE_START_Z

    private const val SURFACE_ZONE_START_Z = 55 * 64
    private val SURFACE_X = 46 * 64 until 53 * 64

    private fun inFeroxEnclave(coord: CoordGrid): Boolean =
        coord.level == 0 && coord.x in FEROX_X && coord.z in FEROX_Z

    // Ferox Enclave's walls, approximately (the wiki map centres it on 3137, 3623).
    private val FEROX_X = 3123..3155
    private val FEROX_Z = 3617..3646
}
