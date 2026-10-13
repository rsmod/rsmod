package org.runerealm.api.player.hit

import org.runerealm.api.player.hit.modifier.PlayerHitModifier
import org.runerealm.game.hit.HitBuilder

public data class DeferredPlayerHit(val builder: HitBuilder, val modifier: PlayerHitModifier)
