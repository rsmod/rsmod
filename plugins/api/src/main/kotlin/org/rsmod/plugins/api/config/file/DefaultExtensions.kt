package org.rsmod.plugins.api.config.file

object DefaultExtensions {

    const val NPC_NAMES = "nname"
    const val ITEM_NAMES = "iname"
    const val OBJ_NAMES = "oname"
    const val COMPONENT_NAMES = "cname"
    const val INTERFACE_NAMES = "uiname"
    const val VARP_NAMES = "vname"
    const val VARBIT_NAMES = "vbname"

    const val NPC_CONFIGS = "npc"
    const val ITEM_CONFIGS = "item"
    const val OBJ_CONFIGS = "obj"

    const val NPC_SPAWNS = "nspawn"

    internal val ALL = arrayOf(
        /* name files */
        NPC_NAMES,
        ITEM_NAMES,
        OBJ_NAMES,
        COMPONENT_NAMES,
        INTERFACE_NAMES,
        VARP_NAMES,
        VARBIT_NAMES,

        /* config files */
        NPC_CONFIGS,
        ITEM_CONFIGS,
        OBJ_CONFIGS,

        /* spawn files */
        NPC_SPAWNS
    )
}
