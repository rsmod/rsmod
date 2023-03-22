package org.rsmod.plugins.net

import org.rsmod.plugins.api.core.GameProcessEvent
import org.rsmod.plugins.api.net.platform.game.GamePlatformPacketMaps
import org.rsmod.plugins.api.subscribe

private val platformPackets: GamePlatformPacketMaps by inject()

subscribe<GameProcessEvent.BootUp> { platformPackets.eagerInitialize() }
