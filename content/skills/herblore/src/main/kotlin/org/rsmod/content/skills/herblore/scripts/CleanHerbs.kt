package org.rsmod.content.skills.herblore.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.script.onOpHeld1
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CleanHerbs
@Inject
constructor() : PluginScript() {

    internal companion object {
        val herbConvertMap: MutableMap<Int, ObjType> = HashMap()
        fun defineMap() {
            herbConvertMap[objs.grimy_guam.id] = objs.clean_guam
            herbConvertMap[objs.grimy_marentill.id] = objs.clean_marentill
            herbConvertMap[objs.grimy_tarromin.id] = objs.clean_tarromin
            herbConvertMap[objs.grimy_harralander.id] = objs.clean_harralander
            herbConvertMap[objs.grimy_ranarr.id] = objs.clean_ranarr
            herbConvertMap[objs.grimy_irit.id] = objs.clean_irit
            herbConvertMap[objs.grimy_avantoe.id] = objs.clean_avantoe
            herbConvertMap[objs.grimy_kwuarm.id] = objs.clean_kwuarm
            herbConvertMap[objs.grimy_cadantine.id] = objs.clean_cadantine
            herbConvertMap[objs.grimy_dwarf_weed.id] = objs.clean_dwarf_weed
            herbConvertMap[objs.grimy_torstol.id] = objs.clean_torstol
        }
        init {
            defineMap()
        }
    }

    override fun ScriptContext.startup() {
        onOpHeld1(content.grimy_herbs) { it ->
            mes("You clicked on herb ${it.type.name}")

            herbConvertMap[it.type.id]?.let {
                replacement -> invReplace(inv, it.type, 1, replacement)
                mes("Output: ${replacement.internalName}")
            }
        }
    }
}
