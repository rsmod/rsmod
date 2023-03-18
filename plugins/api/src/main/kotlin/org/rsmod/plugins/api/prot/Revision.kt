package org.rsmod.plugins.api.prot

public object Revision {

    public const val MAJOR: Int = 212
    public const val MINOR: Int = 1

    public const val LOGIN_MACHINE_INFO_HEADER: Int = 9

    internal inline val GAMEFRAME_MAIN_SCREEN_CHILD: Int get() = 1
    internal inline val GAMEFRAME_HUD_CHILD: Int get() = 2
    internal inline val GAMEFRAME_PVP_CHILD: Int get() = 3
    internal inline val GAMEFRAME_XP_COUNTER_CHILD: Int get() = 9
    internal inline val GAMEFRAME_POPUP_ALERT_CHILD: Int get() = 13
    internal inline val GAMEFRAME_MODAL_CHILD: Int get() = 16
    internal inline val GAMEFRAME_FULL_SCREEN_CHILD: Int get() = 17
    internal inline val GAMEFRAME_MINIMAP_CHILD: Int get() = 32
    internal inline val GAMEFRAME_ATTACK_CHILD: Int get() = 75
    internal inline val GAMEFRAME_SKILLS_CHILD: Int get() = 76
    internal inline val GAMEFRAME_QUEST_CHILD: Int get() = 77
    internal inline val GAMEFRAME_INVENTORY_CHILD: Int get() = 78
    internal inline val GAMEFRAME_EQUIPMENT_CHILD: Int get() = 79
    internal inline val GAMEFRAME_PRAYER_CHILD: Int get() = 80
    internal inline val GAMEFRAME_SPELLS_CHILD: Int get() = 81
    internal inline val GAMEFRAME_CLAN_CHILD: Int get() = 82
    internal inline val GAMEFRAME_MANAGEMENT_CHILD: Int get() = 83
    internal inline val GAMEFRAME_SOCIAL_CHILD: Int get() = 84
    internal inline val GAMEFRAME_LOGOUT_CHILD: Int get() = 85
    internal inline val GAMEFRAME_SETTINGS_CHILD: Int get() = 86
    internal inline val GAMEFRAME_EMOTES_CHILD: Int get() = 87
    internal inline val GAMEFRAME_MUSIC_CHILD: Int get() = 88
    internal inline val GAMEFRAME_USERNAME_CHILD: Int get() = 91
    internal inline val GAMEFRAME_CHATBOX_CHILD: Int get() = 94
}
