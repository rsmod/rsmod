package org.rsmod.plugin.scripts

import jakarta.inject.Inject
import org.rsmod.events.EventBus

public data class ScriptContext @Inject constructor(public val eventBus: EventBus)
