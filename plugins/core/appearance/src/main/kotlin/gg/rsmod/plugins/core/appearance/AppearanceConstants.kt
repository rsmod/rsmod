package gg.rsmod.plugins.core.appearance

import gg.rsmod.game.model.appearance.Appearance
import gg.rsmod.plugins.api.model.appearance.Gender

object AppearanceConstants {

    private const val DEFAULT_GENDER = Gender.MALE

    private const val DEFAULT_SKULL_ICON = -1

    private const val DEFAULT_OVERHEAD_PRAYER = -1

    private const val DEFAULT_NPC_TRANSFORM = -1

    private val DEFAULT_BODY = intArrayOf(
        9, 14, 109, 26, 33, 36, 42
    )

    private val DEFAULT_COLORS = intArrayOf(
        0, 3, 2, 0, 0
    )

    private val DEFAULT_BASE_ANIMATION_SET = intArrayOf(
        808, 823, 819, 820, 821, 822, 824
    )

    val DEFAULT_APPEARANCE = Appearance(
        gender = DEFAULT_GENDER,
        skullIcon = DEFAULT_SKULL_ICON,
        overheadPrayer = DEFAULT_OVERHEAD_PRAYER,
        npcTransform = DEFAULT_NPC_TRANSFORM,
        invisible = false,
        body = DEFAULT_BODY.toList(),
        colors = DEFAULT_COLORS.toList(),
        bas = DEFAULT_BASE_ANIMATION_SET.toList()
    )
}
