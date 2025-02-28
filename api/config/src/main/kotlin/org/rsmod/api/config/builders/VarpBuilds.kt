package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.varp.VarpBuilder

internal object VarpBuilds : VarpBuilder() {
    init {
        build("lastcombat") { temporary = true }
        build("lastcombat_pvp") { temporary = true }
        build("aggressive_npc") { temporary = true }

        build("temp_restore_65527")
        build("generic_temp_coords_65529") { temporary = true }
        build("inv_capacity_65530")
        build("generic_storage_65531")
    }
}
