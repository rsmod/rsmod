package org.rsmod.plugin.scripts

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.cheat.CheatCommandMap
import org.rsmod.game.queue.EngineQueueCache

public class ScriptContext
@Inject
constructor(
    public val eventBus: EventBus,
    public val cheatCommandMap: CheatCommandMap,
    public val engineQueueCache: EngineQueueCache,
)
