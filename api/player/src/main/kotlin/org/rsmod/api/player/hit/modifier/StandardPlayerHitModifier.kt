package org.rsmod.api.player.hit.modifier

public object StandardPlayerHitModifier :
    PlayerHitModifier by StandardPlayerHitPrayerModifier(
        reductionPercentFromNpc = 100,
        reductionPercentFromPlayer = 40,
        reductionPercentFromNoSource = 100,
    )
