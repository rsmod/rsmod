package org.rsmod.content.other.special.attacks.impl

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.content.other.special.attacks.configs.special_seqs
import org.rsmod.content.other.special.attacks.configs.special_spotanims
import org.rsmod.game.type.spot.SpotanimType

class BasicSpecialAttacks
@Inject
constructor(private val worldRepo: WorldRepository, ctx: Context) : SpecialAttackMap(ctx) {
    override fun SpecialAttackRepository.register() {
        registerInstant(objs.dragon_axe, ::lumberUpRed)
        registerInstant(objs.dragon_axe_or, ::lumberUpRed)
        registerInstant(objs.third_age_axe, ::lumberUpSilver)
        registerInstant(objs.infernal_axe, ::lumberUpRed)
        registerInstant(objs.infernal_axe_or, ::lumberUpRed)
        registerInstant(objs.crystal_axe, ::lumberUpSilver)
    }

    private fun lumberUpRed(access: ProtectedAccess) {
        access.lumberUp(special_spotanims.lumber_up_red)
    }

    private fun lumberUpSilver(access: ProtectedAccess) {
        access.lumberUp(special_spotanims.lumber_up_silver)
    }

    private fun ProtectedAccess.lumberUp(spot: SpotanimType) {
        statBoost(stats.woodcutting, constant = 3, percent = 0)
        say("Chop chop!")
        anim(special_seqs.lumber_up)
        spotanim(spot, height = 96, slot = 1)
        worldRepo.soundArea(coords, synths.clobber, radius = 1)
    }
}
