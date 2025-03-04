package org.rsmod.game.entity.npc

import org.rsmod.events.KeyedEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Npc

public class NpcStateEvents {
    public data class Create(val npc: Npc) : UnboundEvent

    public data class Delete(val npc: Npc) : UnboundEvent

    public data class Reveal(val npc: Npc) : UnboundEvent

    public data class Hide(val npc: Npc) : KeyedEvent {
        override val id: Long = npc.id.toLong()
    }

    public data class Spawn(val npc: Npc) : KeyedEvent {
        override val id: Long = npc.id.toLong()
    }
}
