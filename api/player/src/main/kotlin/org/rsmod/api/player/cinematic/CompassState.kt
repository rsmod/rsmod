package org.rsmod.api.player.cinematic

import org.rsmod.api.utils.vars.VarEnumDelegate

/*
 * Unsure what this is really used for aside from hiding the compass "Look DIRECTION" ops.
 * Need to look into it at some point.
 */
public enum class CompassState(override val varValue: Int) : VarEnumDelegate {
    Normal(0),
    Unknown2(2),
    HideOps(3),
}
