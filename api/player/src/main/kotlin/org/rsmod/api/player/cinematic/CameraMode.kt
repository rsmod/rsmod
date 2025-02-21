package org.rsmod.api.player.cinematic

import org.rsmod.api.utils.vars.VarEnumDelegate

public enum class CameraMode(override val varValue: Int) : VarEnumDelegate {
    /** Default camera mode; player can freely zoom in and out. */
    Normal(0),
    /** Camera is zoomed in and manual zooming is disabled. */
    Close(1),
    /** Camera is zoomed out farther and manual zooming is disabled. */
    Far(2),
    /** Camera remains at a fixed middle-ground zoom level and manual zooming is disabled. */
    Fixed(3),
}
