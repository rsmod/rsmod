package org.runerealm.api.controller.events

import org.runerealm.api.controller.access.StandardConAccess
import org.runerealm.events.EventBus
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.game.entity.Controller

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
