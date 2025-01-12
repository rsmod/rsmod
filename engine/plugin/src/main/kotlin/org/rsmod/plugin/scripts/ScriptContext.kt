package org.rsmod.plugin.scripts

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.cheat.CheatCommandMap

public data class ScriptContext
@Inject
constructor(public val eventBus: EventBus, public val cheatCommandMap: CheatCommandMap)
