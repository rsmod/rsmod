package org.rsmod.api.script.advanced

import org.rsmod.api.player.ui.IfButtonDrag
import org.rsmod.api.player.ui.IfOpenTop
import org.rsmod.api.script.onEvent
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onIfOpenTop(type: InterfaceType, action: IfOpenTop.() -> Unit): Unit =
    onEvent<IfOpenTop>(type.id, action)

public fun ScriptContext.onIfButtonDrag(
    selectedComponent: ComponentType,
    targetComponent: ComponentType,
    action: IfButtonDrag.() -> Unit,
) {
    val packed = (selectedComponent.packed.toLong() shl 32) or targetComponent.packed.toLong()
    onEvent(packed, action)
}
