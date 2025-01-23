package org.rsmod.api.controller.events

import org.rsmod.events.KeyedEvent
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
    public class Default(public val controller: Controller, timerType: Int) : KeyedEvent {
        override val id: Long = timerType.toLong()
    }

    public class Type(public val controller: Controller, timerType: Int) : KeyedEvent {
        override val id: Long = (controller.id.toLong() shl 32) or timerType.toLong()
    }
}

public class ControllerQueueEvents {
    public class Default<T>(public val controller: Controller, public val args: T, queueType: Int) :
        KeyedEvent {
        override val id: Long = queueType.toLong()
    }

    public class Type<T>(public val controller: Controller, public val args: T, queueType: Int) :
        KeyedEvent {
        override val id: Long = (controller.id.toLong() shl 32) or queueType.toLong()
    }
}
