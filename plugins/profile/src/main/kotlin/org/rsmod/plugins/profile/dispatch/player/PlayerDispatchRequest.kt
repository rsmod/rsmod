package org.rsmod.plugins.profile.dispatch.player

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.profile.dispatch.DispatchRequest

public data class PlayerDispatchRequest(val player: Player) : DispatchRequest
