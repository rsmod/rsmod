package org.runerealm.content.other.special.attacks

import org.runerealm.api.specials.SpecialAttackMap
import org.runerealm.content.other.special.attacks.boost.StatBoostSpecialAttacks
import org.runerealm.content.other.special.attacks.melee.AbyssalWhipSpecialAttack
import org.runerealm.content.other.special.attacks.melee.DragonClawsSpecialAttack
import org.runerealm.content.other.special.attacks.melee.DragonDaggerSpecialAttack
import org.runerealm.content.other.special.attacks.melee.DragonLongswordSpecialAttack
import org.runerealm.content.other.special.attacks.melee.DragonWarhammerSpecialAttack
import org.runerealm.content.other.special.attacks.melee.GodswordSpecialAttacks
import org.runerealm.content.other.special.attacks.melee.SingleHitMeleeSpecialAttacks
import org.runerealm.content.other.special.attacks.ranged.DarkBowSpecialAttack
import org.runerealm.content.other.special.attacks.ranged.MagicShortbowSpecialAttack
import org.runerealm.plugin.module.PluginModule

class SpecialAttackModule : PluginModule() {
    override fun bind() {
        addSetBinding<SpecialAttackMap>(StatBoostSpecialAttacks::class.java)
        addSetBinding<SpecialAttackMap>(DarkBowSpecialAttack::class.java)
        addSetBinding<SpecialAttackMap>(DragonLongswordSpecialAttack::class.java)
        addSetBinding<SpecialAttackMap>(DragonDaggerSpecialAttack::class.java)
        addSetBinding<SpecialAttackMap>(AbyssalWhipSpecialAttack::class.java)
        addSetBinding<SpecialAttackMap>(SingleHitMeleeSpecialAttacks::class.java)
        addSetBinding<SpecialAttackMap>(DragonWarhammerSpecialAttack::class.java)
        addSetBinding<SpecialAttackMap>(GodswordSpecialAttacks::class.java)
        addSetBinding<SpecialAttackMap>(DragonClawsSpecialAttack::class.java)
        addSetBinding<SpecialAttackMap>(MagicShortbowSpecialAttack::class.java)
    }
}
