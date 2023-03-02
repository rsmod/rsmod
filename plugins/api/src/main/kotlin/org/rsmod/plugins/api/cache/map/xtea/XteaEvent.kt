package org.rsmod.plugins.api.cache.map.xtea

import org.rsmod.game.events.GameEvent

public sealed class XteaEvent : GameEvent {

    public object Loaded : XteaEvent()
}
