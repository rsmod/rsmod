package org.rsmod.api.spells.autocast.scripts

import jakarta.inject.Inject
import org.rsmod.api.spells.autocast.AutocastSpells
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class AutocastScript @Inject constructor(private val spells: AutocastSpells) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        spells.startUp()
    }
}
