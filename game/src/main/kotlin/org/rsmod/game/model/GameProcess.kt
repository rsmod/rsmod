package org.rsmod.game.model

import org.rsmod.game.events.GameEvent

public sealed class GameProcess : GameEvent {

    public object BootUp : GameProcess()
    public object StartCycle : GameProcess()
    public object EndCycle : GameProcess()
}
