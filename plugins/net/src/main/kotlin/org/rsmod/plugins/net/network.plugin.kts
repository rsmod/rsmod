package org.rsmod.plugins.net

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.model.GameProcess
import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps

private val events: GameEventBus by inject()

private val platformPackets: GamePlatformPacketMaps by inject()
events.subscribe<GameProcess.BootUp> { platformPackets.eagerInitialize() }
