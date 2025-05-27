package org.rsmod.api.script.advanced

import org.rsmod.api.player.ui.IfMoveSub
import org.rsmod.api.player.ui.IfMoveTop
import org.rsmod.api.script.onEvent
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onIfMoveTop(type: InterfaceType, action: IfMoveTop.() -> Unit): Unit =
    onEvent<IfMoveTop>(type.id, action)

public fun ScriptContext.onIfMoveSub(dest: ComponentType, action: IfMoveSub.() -> Unit): Unit =
    onEvent<IfMoveSub>(dest.packed, action)
