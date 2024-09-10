@file:Suppress("SpellCheckingInspection", "ConstPropertyName")

package org.rsmod.api.config

public typealias constants = Constants

public object Constants {
    public const val dm_default: String = "Nothing interesting happens."
    public const val dm_reach: String = "I can't reach that!"
    public const val dm_invspace: String = "You don't have enough inventory space to do that."
    public const val dm_busy: String = "Please finish what you are doing first."

    public const val dm_take_taken: String = "Too late - it's gone!"
    public const val dm_take_invspace: String =
        "You don't have enough inventory space to hold that item."

    public const val cm_pausebutton: String = "Click here to continue"
    public const val cm_options: String = "Select an option"
    public const val cm_count: String = "Enter amount:"

    public const val lootdrop_duration: Int = 200
    public const val shop_default_size: Int = 40

    public const val ge_ratelimit_combatkit_uncommon: Int = 15
    public const val ge_ratelimit_combatkit_rare: Int = 8

    // Arbitrary value until I get more info on this var.
    public const val ge_recalcusers_normal: Int = 25
    // Arbitrary value until I get more info on this var.
    public const val ge_recalcusers_low: Int = 10

    public const val modal_fixedwidthandheight: Int = 0
    public const val modal_infinitewidthandheight: Int = 1

    public const val toplevel_prayer: Int = 5
}
