package org.rsmod.api.player.cinematic

public enum class MinimapState(public val id: Int) {
    Normal(0),
    MinimapNoOp(1),
    MinimapHidden(2),
    CompassHidden(3),
    MinimapNoOpCompassHidden(4),
    Disabled(5),
}
