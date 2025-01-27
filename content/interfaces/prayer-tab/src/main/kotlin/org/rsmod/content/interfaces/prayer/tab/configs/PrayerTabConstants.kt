@file:Suppress("ConstPropertyName", "unused")

package org.rsmod.content.interfaces.prayer.tab.configs

internal typealias prayer_constants = PrayerTabConstants

object PrayerTabConstants {
    const val overhead_protect_from_melee = 0
    const val overhead_protect_from_missiles = 1
    const val overhead_protect_from_magic = 2
    const val overhead_retribution = 3
    const val overhead_smite = 4
    const val overhead_redemption = 5
    const val overhead_protect_from_magic_melee = 6
    const val overhead_protect_from_melee_missiles = 7
    const val overhead_protect_from_melee_magic = 8
    const val overhead_protect_from_all_styles = 9
    const val overhead_wrath = 10
    const val overhead_soulsplit = 11
    const val overhead_deflect_melee = 12
    const val overhead_deflect_missiles = 13
    const val overhead_deflect_magic = 14

    fun isOverhead(icon: Int?): Boolean =
        icon in overhead_protect_from_melee..overhead_deflect_magic
}
