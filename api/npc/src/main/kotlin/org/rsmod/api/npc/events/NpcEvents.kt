package org.rsmod.api.npc.events

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Npc

public sealed class NpcEvents {
    public data class Spawn(val npc: Npc) : UnboundEvent

    public data class Delete(val npc: Npc) : UnboundEvent

    public data class Hide(val npc: Npc) : UnboundEvent

    public data class Reveal(val npc: Npc) : UnboundEvent
}
