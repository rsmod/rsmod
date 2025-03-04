package org.rsmod.api.npc.events

import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Npc

public class NpcAIEvents {
    public class Default(public val npc: Npc) : UnboundEvent

    public class Type(public val npc: Npc) : KeyedEvent {
        override val id: Long = npc.id.toLong()
    }

    public class Content(public val npc: Npc, contentGroup: Int) : KeyedEvent {
        override val id: Long = contentGroup.toLong()
    }
}

public class NpcTimerEvents {
    public class Default(public val npc: Npc, timerType: Int) : SuspendEvent<StandardNpcAccess> {
        override val id: Long = timerType.toLong()
    }

    public class Type(public val npc: Npc, timerType: Int) : SuspendEvent<StandardNpcAccess> {
        override val id: Long = (npc.id.toLong() shl 32) or timerType.toLong()
    }

    public class Content(public val npc: Npc, contentGroup: Int, timerType: Int) :
        SuspendEvent<StandardNpcAccess> {
        override val id: Long = (contentGroup.toLong() shl 32) or timerType.toLong()
    }
}

public class NpcQueueEvents {
    public class Default<T>(public val npc: Npc, public val args: T, queueType: Int) :
        SuspendEvent<StandardNpcAccess> {
        override val id: Long = queueType.toLong()
    }

    public class Type<T>(public val npc: Npc, public val args: T, queueType: Int) :
        SuspendEvent<StandardNpcAccess> {
        override val id: Long = (npc.id.toLong() shl 32) or queueType.toLong()
    }

    public class Content<T>(
        public val npc: Npc,
        public val args: T,
        contentGroup: Int,
        queueType: Int,
    ) : SuspendEvent<StandardNpcAccess> {
        override val id: Long = (contentGroup.toLong() shl 32) or queueType.toLong()
    }
}
