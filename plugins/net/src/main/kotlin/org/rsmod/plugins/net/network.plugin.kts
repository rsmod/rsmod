package org.rsmod.plugins.net

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.plugins.api.core.GameProcessEvent
import org.rsmod.plugins.api.net.platform.game.GamePlatformPacketMaps

private val events: GameEventBus by inject()

private val platformPackets: GamePlatformPacketMaps by inject()
events.subscribe<GameProcessEvent.BootUp> { platformPackets.eagerInitialize() }
