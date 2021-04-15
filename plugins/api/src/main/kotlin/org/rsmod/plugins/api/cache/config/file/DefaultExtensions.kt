package org.rsmod.plugins.api.cache.config.file

object DefaultExtensions {

    const val NPC_NAMES = "nname"
    const val ITEM_NAMES = "iname"
    const val OBJ_NAMES = "oname"

    const val NPC_CONFIGS = "npc"
    const val ITEM_CONFIGS = "item"
    const val OBJ_CONFIGS = "obj"

    const val NPC_SPAWNS = "nspawn"

    internal val ALL = arrayOf(
        /* name files */
        NPC_NAMES,
        ITEM_NAMES,
        OBJ_NAMES,

        /* config files */
        NPC_CONFIGS,
        ITEM_CONFIGS,
        OBJ_CONFIGS,

        /* spawn files */
        NPC_SPAWNS
    )
}
