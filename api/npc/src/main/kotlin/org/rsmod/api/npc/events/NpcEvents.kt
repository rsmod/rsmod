package org.rsmod.api.npc.events

import org.rsmod.events.KeyedEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Npc

public class NpcEvents {
    public data class Spawn(val npc: Npc) : UnboundEvent

    public data class Delete(val npc: Npc) : UnboundEvent

    public data class Hide(val npc: Npc) : UnboundEvent

    public data class Reveal(val npc: Npc) : UnboundEvent
}

public class NpcAIEvents {
    public class Default(public val npc: Npc) : UnboundEvent

    public class Type(public val npc: Npc) : KeyedEvent {
        override val id: Long = npc.id.toLong()
    }

    public class Content(public val npc: Npc, contentType: Int) : KeyedEvent {
        override val id: Long = contentType.toLong()
    }
}

public class NpcTimerEvent(public val npc: Npc, timerType: Int) : KeyedEvent {
    override val id: Long = timerType.toLong()
}
