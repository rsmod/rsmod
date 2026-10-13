package org.runerealm.content.skills.woodcutting.configs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.stats
import org.runerealm.api.type.editors.obj.ObjEditor
import org.runerealm.game.type.obj.ObjType

internal object WoodcuttingOutfit : ObjEditor() {
    init {
        outfitXpMod(objs.lumberjack_hat, percent = 4)
        outfitXpMod(objs.forestry_hat, percent = 4)

        outfitXpMod(objs.lumberjack_top, percent = 8)
        outfitXpMod(objs.forestry_top, percent = 8)

        outfitXpMod(objs.lumberjack_legs, percent = 6)
        outfitXpMod(objs.forestry_legs, percent = 6)

        outfitXpMod(objs.lumberjack_boots, percent = 2)
        outfitXpMod(objs.forestry_boots, percent = 2)
    }

    private fun outfitXpMod(type: ObjType, percent: Int) {
        edit(type) {
            param[params.xpmod_stat] = stats.woodcutting
            param[params.xpmod_percent] = percent
        }
    }
}
