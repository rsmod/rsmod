package org.runerealm.api.player.music

import org.runerealm.api.utils.vars.VarEnumDelegate

public enum class MusicPlayMode(override val varValue: Int) : VarEnumDelegate {
    Manual(0),
    Area(1),
    Random(2),
}
