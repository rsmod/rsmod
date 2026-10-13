package org.runerealm.api.player.music

import org.runerealm.api.utils.vars.VarEnumDelegate

public enum class MusicAreaMode(override val varValue: Int) : VarEnumDelegate {
    Modern(0),
    Classic(1),
}
