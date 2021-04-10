package org.rsmod.plugins.api.cache.config.file

object DefaultExtensions {

    const val NPC_CONFIGS = "npc"
    const val ITEM_CONFIGS = "item"
    const val OBJ_CONFIGS = "obj"

    const val NPC_SPAWNS = "nspawn"

    internal val ALL = arrayOf(
        /* config files */
        NPC_CONFIGS,
        ITEM_CONFIGS,
        OBJ_CONFIGS,

        /* spawn files */
        NPC_SPAWNS
    )
}
