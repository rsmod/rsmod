package org.rsmod.api.controller.events

import org.rsmod.api.controller.access.StandardConAccess
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.game.entity.Controller

public class ControllerAIEvents {
    public class Timer(public val controller: Controller) : KeyedEvent {
        override val id: Long = controller.id.toLong()
    }

    public class Queue<T>(public val controller: Controller, public val args: T) : KeyedEvent {
        override val id: Long = controller.id.toLong()
    }
}

public class ControllerTimerEvents {
    public class Default(public val controller: Controller, timerType: Int) :
        SuspendEvent<StandardConAccess> {
        override val id: Long = timerType.toLong()
    }

    public class Type(public val controller: Controller, timerType: Int) :
        SuspendEvent<StandardConAccess> {
        override val id: Long = EventBus.composeLongKey(controller.id, timerType)
    }
}

public class ControllerQueueEvents {
    public class Default<T>(public val controller: Controller, public val args: T, queueType: Int) :
        SuspendEvent<StandardConAccess> {
        override val id: Long = queueType.toLong()
    }

    public class Type<T>(public val controller: Controller, public val args: T, queueType: Int) :
        SuspendEvent<StandardConAccess> {
        override val id: Long = EventBus.composeLongKey(controller.id, queueType)
    }
}
