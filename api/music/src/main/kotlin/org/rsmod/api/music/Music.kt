package org.rsmod.api.music

import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.varp.VarpType

public data class Music(
    val id: Int,
    val displayName: String,
    val unlockHint: String,
    val duration: Int,
    val midi: MidiType,
    val unlockVarp: VarpType?,
    val unlockBitpos: Int,
    val hidden: Boolean,
    val secondary: DbRowType?,
) {
    val unlockBitflag: Int
        get() = 1 shl unlockBitpos

    val canUnlock: Boolean
        get() = unlockVarp != null
}

public data class MusicVariable(val varpIndex: Int, val bitpos: Int)
