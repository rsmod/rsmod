package org.rsmod.api.script

import org.rsmod.api.player.ui.IfCloseSub
import org.rsmod.api.player.ui.IfOpenSub
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onIfOpen(type: InterfaceType, action: IfOpenSub.() -> Unit): Unit =
    onEvent<IfOpenSub>(type.id, action)

public fun ScriptContext.onIfClose(type: InterfaceType, action: IfCloseSub.() -> Unit): Unit =
    onEvent<IfCloseSub>(type.id, action)
