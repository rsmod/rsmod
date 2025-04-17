package org.rsmod.content.skills.woodcutting.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.type.editors.obj.ObjEditor

internal object WoodcuttingOutfit : ObjEditor() {
    init {
        outfitXpMod("ramble_lumberjack_hat", percent = 4)
        outfitXpMod("forestry_lumberjack_hat", percent = 4)

        outfitXpMod("ramble_lumberjack_top", percent = 8)
        outfitXpMod("forestry_lumberjack_top", percent = 8)

        outfitXpMod("ramble_lumberjack_legs", percent = 6)
        outfitXpMod("forestry_lumberjack_legs", percent = 6)

        outfitXpMod("ramble_lumberjack_boots", percent = 2)
        outfitXpMod("forestry_lumberjack_boots", percent = 2)
    }

    private fun outfitXpMod(internal: String, percent: Int) {
        edit(internal) {
            param[params.xpmod_stat] = stats.woodcutting
            param[params.xpmod_percent] = percent
        }
    }
}
