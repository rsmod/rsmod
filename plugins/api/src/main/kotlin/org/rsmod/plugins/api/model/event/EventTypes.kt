package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.Event
import org.rsmod.game.events.KeyedEvent
import org.rsmod.game.model.mob.Player

public typealias TypeGameEvent = Event<Unit>

public typealias TypePlayerEvent = Event<Player>
public typealias TypePlayerKeyedEvent = KeyedEvent<Player>
