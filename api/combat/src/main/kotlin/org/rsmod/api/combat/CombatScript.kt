package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.styles.AttackStyles
import org.rsmod.api.combat.types.AttackTypes
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class CombatScript
@Inject
constructor(private val attackStyles: AttackStyles, private val attackTypes: AttackTypes) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        attackStyles.startUp()
        attackTypes.startUp()
    }
}
