package org.rsmod.game.model.event

import org.rsmod.game.events.Event

public sealed class GameProcess : Event {

    public object StartCycle : GameProcess()
    public object EndCycle : GameProcess()
}
