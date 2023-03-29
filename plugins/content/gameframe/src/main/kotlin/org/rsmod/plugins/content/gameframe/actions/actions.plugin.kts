package org.rsmod.plugins.content.gameframe.actions

import org.rsmod.plugins.api.cache.type.varp.VarpTypeList
import org.rsmod.plugins.api.component
import org.rsmod.plugins.api.getVarp
import org.rsmod.plugins.api.move.MoveSpeed
import org.rsmod.plugins.api.onButton
import org.rsmod.plugins.api.running_enabled
import org.rsmod.plugins.api.setMoveSpeed
import org.rsmod.plugins.api.toggleVarp
import org.rsmod.plugins.api.varp
import org.rsmod.plugins.content.gameframe.gameframe_run_button

private val varps: VarpTypeList by inject()

onButton(component.gameframe_run_button) {
    val speed = when (getVarp(varp.running_enabled)) {
        1 -> MoveSpeed.Walk
        else -> MoveSpeed.Run
    }
    toggleVarp(varps[varp.running_enabled])
    setMoveSpeed(speed)
}
