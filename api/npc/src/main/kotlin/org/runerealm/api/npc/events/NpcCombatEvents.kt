package org.runerealm.api.npc.events

import org.runerealm.events.KeyedEvent
import org.runerealm.game.entity.Npc
import org.runerealm.game.hit.Hit
import org.runerealm.game.hit.HitBuilder

public class NpcHitEvents {
    public data class Modify(
        public val npc: Npc,
        public val hit: HitBuilder,
        override val id: Long = npc.visType.id.toLong(),
    ) : KeyedEvent

    public data class Impact(
        public val npc: Npc,
        public val hit: Hit,
        override val id: Long = npc.visType.id.toLong(),
    ) : KeyedEvent
}
