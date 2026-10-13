package org.runerealm.api.game.process

import org.runerealm.events.UnboundEvent

public sealed class GameLifecycle : UnboundEvent {
    public data object Startup : GameLifecycle()

    public data object StartCycle : GameLifecycle()

    public data object LateCycle : GameLifecycle()

    public data object EndCycle : GameLifecycle()

    public data object Shutdown : GameLifecycle()

    public data object UpdateInfo : GameLifecycle()
}
