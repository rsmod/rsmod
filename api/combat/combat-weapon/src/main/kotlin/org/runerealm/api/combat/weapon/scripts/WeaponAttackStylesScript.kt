package org.runerealm.api.combat.weapon.scripts

import jakarta.inject.Inject
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class WeaponAttackStylesScript @Inject constructor(private val attackStyles: AttackStyles) :
    PluginScript() {
    override fun ScriptContext.startup() {
        attackStyles.startup()
    }
}
