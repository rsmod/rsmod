package org.runerealm.plugin.scripts

import jakarta.inject.Inject
import org.runerealm.events.EventBus
import org.runerealm.game.cheat.CheatCommandMap
import org.runerealm.game.queue.EngineQueueCache

public class ScriptContext
@Inject
constructor(
    public val eventBus: EventBus,
    public val cheatCommandMap: CheatCommandMap,
    public val engineQueueCache: EngineQueueCache,
)
