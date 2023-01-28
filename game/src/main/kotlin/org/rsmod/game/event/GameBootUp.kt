package org.rsmod.game.event

import org.rsmod.game.events.Event

/**
 * An [Event] to be published _before_ all bound [com.google.common.util.concurrent.Service]s start up.
 */
public object GameBootUp : Event
