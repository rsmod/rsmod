package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.varp.VarpBuilder

internal object VarpBuilds : VarpBuilder() {
    init {
        build("music_playlist") { temporary = true }
        build("music_temp_state_3") { temporary = true }
        build("music_temp_state_2") { temporary = true }
        build("music_temp_state_1") { temporary = true }

        build("gameframe")

        build("pk_prey1") { temporary = true }
        build("pk_prey2") { temporary = true }
        build("pk_predator1") { temporary = true }
        build("pk_predator2") { temporary = true }
        build("pk_predator3") { temporary = true }

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
