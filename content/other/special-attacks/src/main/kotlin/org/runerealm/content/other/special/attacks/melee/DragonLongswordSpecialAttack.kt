package org.runerealm.content.other.special.attacks.melee

import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.objs
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.specials.SpecialAttackManager
import org.runerealm.api.specials.SpecialAttackMap
import org.runerealm.api.specials.SpecialAttackRepository
import org.runerealm.api.specials.combat.MeleeSpecialAttack
import org.runerealm.content.other.special.attacks.configs.special_seqs
import org.runerealm.content.other.special.attacks.configs.special_spots
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player

class DragonLongswordSpecialAttack : SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerMelee(objs.dragon_longsword, DragonLongsword(manager))
    }

    private class DragonLongsword(private val manager: SpecialAttackManager) : MeleeSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean {
            cleave(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean {
            cleave(target, attack)
            return true
        }

        private fun ProtectedAccess.cleave(target: PathingEntity, attack: CombatAttack.Melee) {
            anim(special_seqs.dragon_longsword)
            spotanim(
                spot = special_spots.dragon_longsword,
                slot = constants.spotanim_slot_combat,
                height = 96,
            )

            val damage =
                manager.rollMeleeDamage(
                    source = this,
                    target = target,
                    attack = attack,
                    accuracyMultiplier = 1.25,
                    maxHitMultiplier = 1.25,
                    blockType = MeleeAttackType.Slash,
                )
            manager.giveCombatXp(this, target, attack, damage)
            manager.queueMeleeHit(this, target, damage)
            manager.continueCombat(this, target)
        }
    }
}
