package org.runerealm.api.spells.autocast.scripts

import jakarta.inject.Inject
import org.runerealm.api.spells.autocast.AutocastSpells
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class AutocastScript @Inject constructor(private val spells: AutocastSpells) :
    PluginScript() {
    override fun ScriptContext.startup() {
        spells.startup()
    }
}
