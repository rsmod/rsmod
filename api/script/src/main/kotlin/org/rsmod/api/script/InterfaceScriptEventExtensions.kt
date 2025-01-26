package org.rsmod.api.script

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.ui.IfCloseSub
import org.rsmod.api.player.ui.IfModalButton
import org.rsmod.api.player.ui.IfModalDrag
import org.rsmod.api.player.ui.IfOpenSub
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.IfOverlayDrag
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onIfOpen(type: InterfaceType, action: IfOpenSub.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onIfClose(type: InterfaceType, action: IfCloseSub.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onIfOverlayButton(
    button: ComponentType,
    action: IfOverlayButton.() -> Unit,
): Unit = onEvent(button.packed, action)

public fun ScriptContext.onIfModalButton(
    button: ComponentType,
    action: suspend ProtectedAccess.(IfModalButton) -> Unit,
): Unit = onProtectedEvent(button.packed, action)

public fun ScriptContext.onIfOverlayDrag(
    selectedComponent: ComponentType,
    targetComponent: ComponentType = selectedComponent,
    action: IfOverlayDrag.() -> Unit,
) {
    val packed = (selectedComponent.packed.toLong() shl 32) or targetComponent.packed.toLong()
    onEvent(packed, action)
}

public fun ScriptContext.onIfModalDrag(
    selectedComponent: ComponentType,
    targetComponent: ComponentType = selectedComponent,
    action: suspend ProtectedAccess.(IfModalDrag) -> Unit,
) {
    val packed = (selectedComponent.packed.toLong() shl 32) or targetComponent.packed.toLong()
    onProtectedEvent(packed, action)
}
