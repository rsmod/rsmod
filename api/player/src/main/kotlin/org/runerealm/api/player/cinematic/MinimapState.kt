package org.runerealm.api.player.cinematic

import org.runerealm.api.utils.vars.VarEnumDelegate

public enum class MinimapState(override val varValue: Int) : VarEnumDelegate {
    Normal(0),
    MinimapNoOp(1),
    MinimapHidden(2),
    CompassHidden(3),
    MinimapNoOpCompassHidden(4),
    Disabled(5),
}
