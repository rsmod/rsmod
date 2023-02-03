package org.rsmod.plugins.store.dev.data

import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.store.player.PlayerCodecData

public data class DevPlayerData(
    val username: String,
    val displayName: String,
    val coords: Coordinates
) : PlayerCodecData
