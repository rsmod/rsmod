package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.IfOpenTop
import org.rsmod.api.script.onEvent
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onIfOpenTop(type: InterfaceType, action: IfOpenTop.() -> Unit): Unit =
    onEvent<IfOpenTop>(type.id, action)
