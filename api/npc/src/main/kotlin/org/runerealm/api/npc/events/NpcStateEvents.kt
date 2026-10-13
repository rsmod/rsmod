package org.runerealm.api.npc.events

import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.events.EventBus
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Npc

public class AiTimerEvents {
    public class Default(public val npc: Npc) : UnboundEvent

    public class Type(public val npc: Npc) : KeyedEvent {
        override val id: Long = npc.id.toLong()
    }

    public class Content(public val npc: Npc, contentGroup: Int) : KeyedEvent {
        override val id: Long = contentGroup.toLong()
    }
}

public class AiQueueEvents {
    public class Default(public val npc: Npc, queueType: Int) : KeyedEvent {
        override val id: Long = queueType.toLong()
    }

    public class Type(public val npc: Npc, queueType: Int) : KeyedEvent {
        override val id: Long = EventBus.composeLongKey(npc.id, queueType)
    }

    public class Content(public val npc: Npc, queueType: Int, contentGroup: Int) : KeyedEvent {
        override val id: Long = EventBus.composeLongKey(contentGroup, queueType)
    }
}

public class NpcTimerEvents {
    public class Default(public val npc: Npc, timerType: Int) : SuspendEvent<StandardNpcAccess> {
        override val id: Long = timerType.toLong()
    }

    public class Type(public val npc: Npc, timerType: Int) : SuspendEvent<StandardNpcAccess> {
        override val id: Long = EventBus.composeLongKey(npc.id, timerType)
    }

    public class Content(public val npc: Npc, contentGroup: Int, timerType: Int) :
        SuspendEvent<StandardNpcAccess> {
        override val id: Long = EventBus.composeLongKey(contentGroup, timerType)
    }
}

public class NpcQueueEvents {
    public class Default<T>(public val npc: Npc, public val args: T, queueType: Int) :
        SuspendEvent<StandardNpcAccess> {
        override val id: Long = queueType.toLong()
    }

    public class Type<T>(public val npc: Npc, public val args: T, queueType: Int) :
        SuspendEvent<StandardNpcAccess> {
        override val id: Long = EventBus.composeLongKey(npc.id, queueType)
    }

    public class Content<T>(
        public val npc: Npc,
        public val args: T,
        contentGroup: Int,
        queueType: Int,
    ) : SuspendEvent<StandardNpcAccess> {
        override val id: Long = EventBus.composeLongKey(contentGroup, queueType)
    }
}
