package org.rsmod.content.skills.herblore.config

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.obj.ObjType

typealias herbs = HerbReferences

internal object HerbObjs : ObjEditor() {
    init {
        herbs.grimyLocs.forEach { it ->
            configureGrimy(it)
        }
        herbs.cleanLocs.forEach { it ->
            configureClean(it)
        }
    }

    private fun configureGrimy(obj: ObjType) {
        edit(obj) {
            contentGroup = content.grimy_herbs
        }
    }

    private fun configureClean(obj: ObjType) {
        edit(obj) {
            contentGroup = content.clean_herbs
        }
    }
}

object HerbReferences : ObjReferences() {
    val grimyLocs =
        listOf(
            find("unidentified_guam"),
            find("unidentified_marentill"),
            find("unidentified_tarromin"),
            find("unidentified_harralander"),
            find("unidentified_ranarr"),
            find("unidentified_irit"),
            find("unidentified_avantoe"),
            find("unidentified_kwuarm"),
            find("unidentified_cadantine"),
            find("unidentified_dwarf_weed"),
            find("unidentified_torstol"),
        )
    val cleanLocs =
        listOf(
            find("guam_leaf"),
            find("marentill"),
            find("tarromin"),
            find("harralander"),
            find("ranarr_weed"),
            find("irit_leaf"),
            find("avantoe"),
            find("kwuarm"),
            find("cadantine"),
            find("dwarf_weed"),
            find("torstol"),
        )
}

