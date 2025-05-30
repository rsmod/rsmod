package org.rsmod.api.player.hit

import org.rsmod.api.player.hit.modifier.PlayerHitModifier
import org.rsmod.game.hit.HitBuilder

public data class DeferredPlayerHit(val builder: HitBuilder, val modifier: PlayerHitModifier)
