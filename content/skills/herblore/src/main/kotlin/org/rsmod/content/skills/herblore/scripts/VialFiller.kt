package org.rsmod.content.skills.herblore.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpHeld4
import org.rsmod.api.script.onOpLocU
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class VialFiller
@Inject
constructor(private val locRepo: LocRepository,
    private val objTypeList: ObjTypeList) : PluginScript() {

    override fun ScriptContext.startup() {
        onOpLocU(content.fountains, objs.vial_empty) {
            val vials = inv.count(objTypeList[objs.vial_empty])
            invReplace(inv, objs.vial_empty, vials, objs.vial_water)
            player.anim(seqs.human_pickuptable, 0, 0)
            player.soundSynth(synths.fill_vial)
            mes("You filled $vials empty vials with water.")
        }

        onOpHeld4(objs.vial_water) {
            invReplace(inv, objs.vial_water, 1, objs.vial_empty)
        }
    }

}
