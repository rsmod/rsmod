package org.rsmod.content.other.generic

import jakarta.inject.Inject
import org.rsmod.content.other.generic.npcs.GenericPerson
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.plugin.scripts.SimplePluginScript

class GenericScripts @Inject constructor(private val genericPerson: GenericPerson) :
    SimplePluginScript() {
    override fun ScriptContext.startUp() {
        genericPerson.startUp(this)
    }
}
