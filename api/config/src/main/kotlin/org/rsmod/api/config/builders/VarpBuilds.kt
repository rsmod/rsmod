package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.varp.VarpBuilder

internal object VarpBuilds : VarpBuilder() {
    init {
        build("pk_prey1")
        build("pk_prey2")
        build("pk_predator1")
        build("pk_predator2")
        build("pk_predator3")

        build("prayer_drain") { temporary = true }
        build("playtime")
        build("generic_temp_state_65516") { temporary = true }
        build("dinhs_passive_delay") { temporary = true }
        build("com_maxhit")
        build("forinthry_surge_expiration")
        build("saved_autocast_state_staff")
        build("saved_autocast_state_bladed_staff")
        build("lastcombat") { temporary = true }
        build("lastcombat_pvp") { temporary = true }
        build("aggressive_npc") { temporary = true }

        build("temp_restore_65527")
        build("generic_temp_coords_65529") { temporary = true }
        build("inv_capacity_65530")
        build("generic_storage_65531")
    }
}
