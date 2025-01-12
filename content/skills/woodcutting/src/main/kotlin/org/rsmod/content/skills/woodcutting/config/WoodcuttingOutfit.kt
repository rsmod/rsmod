package org.rsmod.content.skills.woodcutting.config

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.type.editors.obj.ObjEditor

internal object WoodcuttingOutfit : ObjEditor() {
    init {
        outfitXpMod("lumberjack_hat", percent = 4)
        outfitXpMod("forestry_hat", percent = 4)

        outfitXpMod("lumberjack_top", percent = 8)
        outfitXpMod("forestry_top", percent = 8)

        outfitXpMod("lumberjack_legs", percent = 6)
        outfitXpMod("forestry_legs", percent = 6)

        outfitXpMod("lumberjack_boots", percent = 2)
        outfitXpMod("forestry_boots", percent = 2)
    }

    private fun outfitXpMod(internal: String, percent: Int) {
        edit(internal) {
            param[params.xpmod_stat] = stats.woodcutting
            param[params.xpmod_percent] = percent
        }
    }
}
