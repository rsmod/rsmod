package org.rsmod.content.other.special.attacks.boost

import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.content.other.special.attacks.configs.special_seqs
import org.rsmod.content.other.special.attacks.configs.special_spots
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType

class StatBoostSpecialAttacks @Inject constructor(private val worldRepo: WorldRepository) :
    SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerInstant(objs.dragon_axe, ::lumberUpRed)
        registerInstant(objs.dragon_axe_or, ::lumberUpRed)
        registerInstant(objs.third_age_axe, ::lumberUpSilver)
        registerInstant(objs.infernal_axe, ::lumberUpRed)
        registerInstant(objs.infernal_axe_or, ::lumberUpRed)
        registerInstant(objs.crystal_axe, ::lumberUpSilver)

        registerInstant(objs.dragon_harpoon, ::fishstabberDragonHarpoon)
        registerInstant(objs.dragon_harpoon_or, ::fishstabberDragonHarpoonOr)
        registerInstant(objs.infernal_harpoon, ::fishstabberInfernalHarpoon)
        registerInstant(objs.infernal_harpoon_or, ::fishstabberInfernalHarpoonOr)
        registerInstant(objs.infernal_harpoon_or_uncharged, ::fishstabberInfernalHarpoonOr)
        registerInstant(objs.crystal_harpoon, ::fishstabberCrystalHarpoon)

        registerInstant(objs.dragon_pickaxe, ::rockKnockerDragonPickaxe)
        registerInstant(objs.dragon_pickaxe_or_zalcano, ::rockKnockerDragonPickaxeOrZalcano)
        registerInstant(objs.dragon_pickaxe_or_trailblazer, ::rockKnockerDragonPickaxeOrTrailblazer)
        registerInstant(objs.dragon_pickaxe_upgraded, ::rockKnockerDragonPickaxeUpgraded)
        registerInstant(objs.infernal_pickaxe, ::rockKnockerInfernalPickaxe)
        registerInstant(objs.infernal_pickaxe_uncharged, ::rockKnockerInfernalPickaxe)
        registerInstant(objs.infernal_pickaxe_or, ::rockKnockerInfernalPickaxeOr)
        registerInstant(objs.infernal_pickaxe_or_uncharged, ::rockKnockerInfernalPickaxeOr)
        registerInstant(objs.third_age_pickaxe, ::rockKnockerThirdAgePickaxe)
        registerInstant(objs.crystal_pickaxe, ::rockKnockerCrystalPickaxe)
    }

    private fun lumberUpRed(access: ProtectedAccess): Boolean {
        return access.lumberUp(special_spots.lumber_up_red)
    }

    private fun lumberUpSilver(access: ProtectedAccess): Boolean {
        return access.lumberUp(special_spots.lumber_up_silver)
    }

    private fun ProtectedAccess.lumberUp(spot: SpotanimType): Boolean {
        statBoost(stats.woodcutting, constant = 3, percent = 0)
        say("Chop chop!")
        anim(special_seqs.lumber_up)
        spotanim(spot, height = 96, slot = constants.spotanim_slot_combat)
        soundArea(worldRepo, coords, synths.clobber, radius = 1)
        return true
    }

    private fun fishstabberDragonHarpoon(access: ProtectedAccess): Boolean {
        return access.fishstabber(special_seqs.fishstabber_dragon_harpoon, spotanims.portal_red)
    }

    private fun fishstabberDragonHarpoonOr(access: ProtectedAccess): Boolean {
        // Uses same seq as Infernal harpoon (or).
        return access.fishstabber(
            special_seqs.fishstabber_infernal_harpoon_or,
            spotanims.portal_red,
        )
    }

    private fun fishstabberInfernalHarpoon(access: ProtectedAccess): Boolean {
        return access.fishstabber(special_seqs.fishstabber_infernal_harpoon, spotanims.portal_red)
    }

    private fun fishstabberInfernalHarpoonOr(access: ProtectedAccess): Boolean {
        return access.fishstabber(
            special_seqs.fishstabber_infernal_harpoon_or,
            spotanims.portal_red,
        )
    }

    private fun fishstabberCrystalHarpoon(access: ProtectedAccess): Boolean {
        return access.fishstabber(
            special_seqs.fishstabber_crystal_harpoon,
            special_spots.fishstabber_silver,
        )
    }

    private fun ProtectedAccess.fishstabber(seq: SeqType, spot: SpotanimType): Boolean {
        statBoost(stats.fishing, constant = 3, percent = 0)
        say("Here fishy fishies!")
        anim(seq)
        spotanim(spot)
        soundArea(worldRepo, coords, synths.rampage, radius = 1)
        return true
    }

    private fun rockKnockerDragonPickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe)
    }

    private fun rockKnockerDragonPickaxeOrTrailblazer(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_or_trailblazer)
    }

    private fun rockKnockerDragonPickaxeOrZalcano(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_or_zalcano)
    }

    private fun rockKnockerDragonPickaxeUpgraded(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_upgraded)
    }

    private fun rockKnockerInfernalPickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_infernal_pickaxe)
    }

    private fun rockKnockerInfernalPickaxeOr(access: ProtectedAccess): Boolean {
        // Uses same seq as Dragon pickaxe (or).
        return access.rockKnocker(special_seqs.rock_knocker_dragon_pickaxe_or_trailblazer)
    }

    private fun rockKnockerThirdAgePickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_3rd_age_pickaxe)
    }

    private fun rockKnockerCrystalPickaxe(access: ProtectedAccess): Boolean {
        return access.rockKnocker(special_seqs.rock_knocker_crystal_pickaxe)
    }

    private fun ProtectedAccess.rockKnocker(seq: SeqType): Boolean {
        statBoost(stats.mining, constant = 3, percent = 0)
        say("Smashing!")
        anim(seq)
        soundArea(worldRepo, coords, synths.found_gem, radius = 1)
        return true
    }
}
