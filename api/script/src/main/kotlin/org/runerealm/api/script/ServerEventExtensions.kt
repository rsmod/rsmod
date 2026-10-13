package org.runerealm.api.script

import org.runerealm.api.game.process.GameLifecycle
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onGameStartup(action: GameLifecycle.Startup.() -> Unit): Unit =
    onEvent<GameLifecycle.Startup>(action)
