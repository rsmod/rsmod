package org.rsmod.api.script

import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onBootUp(action: GameLifecycle.BootUp.() -> Unit): Unit =
    onEvent<GameLifecycle.BootUp>(action)
