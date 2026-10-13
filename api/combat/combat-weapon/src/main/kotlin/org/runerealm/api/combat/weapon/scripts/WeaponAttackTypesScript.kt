package org.runerealm.api.combat.weapon.scripts

import jakarta.inject.Inject
import org.runerealm.api.combat.weapon.types.AttackTypes
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class WeaponAttackTypesScript @Inject constructor(private val attackTypes: AttackTypes) :
    PluginScript() {
    override fun ScriptContext.startup() {
        attackTypes.startup()
    }
}
