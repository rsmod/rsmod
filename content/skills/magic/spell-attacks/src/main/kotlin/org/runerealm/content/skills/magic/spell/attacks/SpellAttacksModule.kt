package org.runerealm.content.skills.magic.spell.attacks

import org.runerealm.api.spells.attack.SpellAttackMap
import org.runerealm.content.skills.magic.spell.attacks.standard.ElementalSpells
import org.runerealm.plugin.module.PluginModule

class SpellAttacksModule : PluginModule() {
    override fun bind() {
        addSetBinding<SpellAttackMap>(ElementalSpells::class.java)
    }
}
