package org.rsmod.api.player.hit.modifier

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitBuilder
import org.rsmod.game.hit.HitType

public object StandardPlayerHitModifier :
    PlayerHitModifier by StandardPlayerHitPrayerModifier(
        reductionPercentFromNpc = 100,
        reductionPercentFromPlayer = 40,
        reductionPercentFromNoSource = 100,
    )

public class StandardPlayerHitPrayerModifier(
    private val reductionPercentFromNpc: Int,
    private val reductionPercentFromPlayer: Int,
    private val reductionPercentFromNoSource: Int,
) : PlayerHitModifier {
    private val Player.protectFromMagic by boolVarBit(varbits.protect_from_magic)
    private val Player.protectFromRanged by boolVarBit(varbits.protect_from_missiles)
    private val Player.protectFromMelee by boolVarBit(varbits.protect_from_melee)

    public constructor(
        reductionPercent: Int
    ) : this(reductionPercent, reductionPercent, reductionPercent)

    init {
        val npc = reductionPercentFromNpc
        require(npc in 0..100) {
            "`reductionPercentFromNpc` must be between [0..100]. (reduction=$npc)"
        }

        val player = reductionPercentFromPlayer
        require(player in 0..100) {
            "`reductionPercentFromPlayer` must be between [0..100]. (reduction=$player)"
        }

        val noSource = reductionPercentFromNoSource
        require(noSource in 0..100) {
            "`reductionPercentFromNoSource` must be between [0..100]. (reduction=$noSource)"
        }
    }

    override fun HitBuilder.modify(target: Player) {
        val protectionPrayer =
            when (type) {
                HitType.Typeless -> false
                HitType.Melee -> target.protectFromMelee
                HitType.Ranged -> target.protectFromRanged
                HitType.Magic -> target.protectFromMagic
            }

        if (protectionPrayer) {
            val reduction =
                when {
                    isFromNpc -> reductionPercentFromNpc
                    isFromPlayer -> reductionPercentFromPlayer
                    else -> reductionPercentFromNoSource
                }
            val reduced = ((damage * 100) - (damage * reduction)) / 100
            damage = reduced
        }
    }
}
