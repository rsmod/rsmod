package org.runerealm.api.script

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.ui.IfCloseSub
import org.runerealm.api.player.ui.IfModalButton
import org.runerealm.api.player.ui.IfModalButtonT
import org.runerealm.api.player.ui.IfModalDrag
import org.runerealm.api.player.ui.IfOpenSub
import org.runerealm.api.player.ui.IfOverlayButton
import org.runerealm.api.player.ui.IfOverlayButtonT
import org.runerealm.api.player.ui.IfOverlayDrag
import org.runerealm.api.player.ui.IfOverlaySubOp
import org.runerealm.events.EventBus
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onIfOpen(type: InterfaceType, action: IfOpenSub.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onIfClose(type: InterfaceType, action: IfCloseSub.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onIfOverlayButton(
    button: ComponentType,
    action: IfOverlayButton.() -> Unit,
): Unit = onEvent(button.packed, action)

/** Registers a script for sub-ops (`IF_SUBOP`) selected on an _overlay_ interface component. */
public fun ScriptContext.onIfOverlaySubOp(
    button: ComponentType,
    action: IfOverlaySubOp.() -> Unit,
): Unit = onEvent(button.packed, action)

public fun ScriptContext.onIfModalButton(
    button: ComponentType,
    action: suspend ProtectedAccess.(IfModalButton) -> Unit,
): Unit = onProtectedEvent(button.packed, action)

/**
 * Registers a script that triggers when an _overlay_ interface component targets another component.
 */
public fun ScriptContext.onIfOverlayButtonT(
    selectedComponent: ComponentType,
    targetComponent: ComponentType = selectedComponent,
    action: IfOverlayButtonT.() -> Unit,
) {
    val packed = EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)
    onEvent(packed, action)
}

/**
 * Registers a script that triggers when a _modal_ interface component targets another component.
 */
public fun ScriptContext.onIfModalButtonT(
    selectedComponent: ComponentType,
    targetComponent: ComponentType = selectedComponent,
    action: suspend ProtectedAccess.(IfModalButtonT) -> Unit,
) {
    val packed = EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)
    onProtectedEvent(packed, action)
}

public fun ScriptContext.onIfOverlayDrag(
    selectedComponent: ComponentType,
    targetComponent: ComponentType = selectedComponent,
    action: IfOverlayDrag.() -> Unit,
) {
    val packed = EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)
    onEvent(packed, action)
}

public fun ScriptContext.onIfModalDrag(
    selectedComponent: ComponentType,
    targetComponent: ComponentType = selectedComponent,
    action: suspend ProtectedAccess.(IfModalDrag) -> Unit,
) {
    val packed = EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)
    onProtectedEvent(packed, action)
}
