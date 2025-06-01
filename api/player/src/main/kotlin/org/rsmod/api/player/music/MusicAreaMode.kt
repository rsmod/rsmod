package org.rsmod.api.player.music

import org.rsmod.api.utils.vars.VarEnumDelegate

public enum class MusicAreaMode(override val varValue: Int) : VarEnumDelegate {
    Modern(0),
    Classic(1),
}
