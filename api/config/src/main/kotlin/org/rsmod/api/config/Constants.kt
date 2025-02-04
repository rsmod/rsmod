@file:Suppress("SpellCheckingInspection", "unused", "ConstPropertyName")

package org.rsmod.api.config

typealias constants = Constants

object Constants {
    const val dm_default: String = "Nothing interesting happens."
    const val dm_reach: String = "I can't reach that!"
    const val dm_invspace: String = "You don't have enough inventory space to do that."
    const val dm_busy: String = "Please finish what you are doing first."

    const val dm_take_taken: String = "Too late - it's gone!"
    const val dm_take_invspace: String = "You don't have enough inventory space to hold that item."

    const val cm_pausebutton: String = "Click here to continue"
    const val cm_options: String = "Select an option"
    const val cm_count: String = "Enter amount:"
    const val cm_obj: String = "Select an item:"

    const val lootdrop_duration: Int = 200
    const val shop_default_size: Int = 40

    const val ge_ratelimit_combatkit_uncommon: Int = 15
    const val ge_ratelimit_combatkit_rare: Int = 8

    // Arbitrary value until I get more info on this var.
    const val ge_recalcusers_normal: Int = 25
    // Arbitrary value until I get more info on this var.
    const val ge_recalcusers_low: Int = 10

    const val meslayer_mode_countdialog = 7
    const val meslayer_mode_objsearch = 11
    const val meslayer_mode_objdialog = 14

    const val modal_fixedwidthandheight: Int = 0
    const val modal_infinitewidthandheight: Int = 1

    const val toplevel_details: Int = 2
    const val toplevel_prayer: Int = 5

    const val overlay_timer_woodcutting: Int = 2

    const val combat_default_attackrate = 4

    const val setpos_abs_top = 0
    const val setpos_abs_left = 0
    const val setpos_abs_centre = 1
    const val setpos_abs_bottom = 2
    const val setpos_abs_right = 2

    const val setsize_abs = 0
    const val setsize_minus = 1

    const val settextalign_top = 0
    const val settextalign_left = 0
    const val settextalign_centre = 1
    const val settextalign_bottom = 2
    const val settextalign_right = 2
}
