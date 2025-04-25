package org.rsmod.api.script

import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onGameStartup(action: GameLifecycle.Startup.() -> Unit): Unit =
    onEvent<GameLifecycle.Startup>(action)
