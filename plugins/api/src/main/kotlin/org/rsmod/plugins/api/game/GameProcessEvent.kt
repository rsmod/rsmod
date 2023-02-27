package org.rsmod.plugins.api.game

import org.rsmod.game.events.GameEvent

public sealed class GameProcessEvent : GameEvent {

    public object BootUp : GameProcessEvent()
    public object StartCycle : GameProcessEvent()
    public object EndCycle : GameProcessEvent()
}
