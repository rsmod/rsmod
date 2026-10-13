package org.runerealm.api.script.advanced

import org.runerealm.api.player.ui.IfMoveSub
import org.runerealm.api.player.ui.IfMoveTop
import org.runerealm.api.script.onEvent
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onIfMoveTop(type: InterfaceType, action: IfMoveTop.() -> Unit): Unit =
    onEvent<IfMoveTop>(type.id, action)

public fun ScriptContext.onIfMoveSub(dest: ComponentType, action: IfMoveSub.() -> Unit): Unit =
    onEvent<IfMoveSub>(dest.packed, action)
