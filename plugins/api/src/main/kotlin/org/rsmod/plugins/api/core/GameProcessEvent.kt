package org.rsmod.plugins.api.core

import org.rsmod.plugins.api.model.event.TypeGameEvent

public sealed class GameProcessEvent : TypeGameEvent {

    public object BootUp : GameProcessEvent()
    public object StartCycle : GameProcessEvent()
    public object EndCycle : GameProcessEvent()
}
