@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varp.VarpReferences

typealias varps = BaseVarps

object BaseVarps : VarpReferences() {
    /*
     * These "generic" temporary-state varps are used across multiple interfaces to track temporary
     * state. Unlike varps tied to a specific piece of content with child varbits, these are more
     * general-purpose.
     */
    val if1 = find("if1", 59242162)
    val if2 = find("if2", 59469143)
    val if3 = find("if3", 59696124)

    val canoeing_menu = find("canoeing_menu", 153212296)

    val com_mode = find("com_mode", 9764025)
    // Note: This varp seems to only be transmitted while wielding melee weapons and correlates to
    // Controlled (0), Accurate (1), Aggressive (2), and Defensive (3).
    // Though it may be the case that it actually represents the current "XP" type being granted.
    val com_stance = find("com_stance", 10444968)
    val option_nodef = find("option_nodef", 39044574)
    val sa_energy = find("sa_energy", 68098142)
    val sa_attack = find("sa_attack", 68325123)
    val soulreaper_souls = find("soulreaper_stacks", 858899946)

    val option_run = find("option_run", 39271563)
    val option_attackpriority = find("option_attackpriority", 251271828)
    val option_attackpriority_npc = find("option_attackpriority_npc", 296441051)
    val option_sounds = find("option_sounds", 38363631)

    val cookquest = find("cookquest", 6586291)
    val doricquest = find("doricquest", 7040253)
    val haunted = find("haunted", 7267234)
    val runemysteries = find("runemysteries", 14303645)
    val hetty = find("hetty", 15211569)
    val hunt = find("hunt", 16119493)
    val rjquest = find("rjquest", 32689106)
    val imp = find("imp", 36320802)
    val dragonquest = find("dragonquest", 39952498)
    val vampire = find("vampire", 40406460)
    val sheep = find("sheep", 40633441)

    val colosseum_glory = find("colosseum_glory", 937435372)

    /* Server-side-only types */
    val generic_temp_state_65516 = find("generic_temp_state_65516")
    val dinhs_passive_delay = find("dinhs_passive_delay")
    val com_maxhit = find("com_maxhit")
    val forinthry_surge_expiration = find("forinthry_surge_expiration")
    val saved_autocast_state_staff = find("saved_autocast_state_staff")
    val saved_autocast_state_bladed_staff = find("saved_autocast_state_bladed_staff")
    val lastcombat = find("lastcombat")
    val lastcombat_pvp = find("lastcombat_pvp")
    val aggressive_npc = find("aggressive_npc")
    val generic_temp_coords_65529 = find("generic_temp_coords_65529")
    val inv_capacity_65530 = find("inv_capacity_65530")
    val generic_storage_65531 = find("generic_storage_65531")

    /*
     * "Restore" varps serve as temporary storage for varps that are modified temporarily and need
     * to be restored later.
     */
    val temp_restore_65527 = find("temp_restore_65527")
}
