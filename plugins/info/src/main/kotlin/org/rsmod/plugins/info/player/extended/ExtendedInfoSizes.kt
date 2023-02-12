package org.rsmod.plugins.info.player.extended

public object ExtendedInfoSizes {

    private const val USERNAME_MAX_BYTE_LENGTH = 13 /* max username string length + 1 (to write size) */
    private const val MAX_HIT_COUNT = 255 /* max amount of hits that can be sent to client */
    private const val PER_HIT_BYTE_LENGTH = 19 /* 19 bytes per hit */
    private const val APPEARANCE_LOOK_MAX_BYTE_LENGTH = 24 /* 2 bytes per element */
    private const val APPEARANCE_COLORS_MAX_BYTE_LENGTH = 5 /* 1 byte per element */
    private const val APPEARANCE_BAS_MAX_BYTE_LENGTH = 14 /* 2 bytes per element */
    private const val APPEARANCE_TAG_MAX_BYTE_LENGTH = 12 /* 12 bytes per prefix tag */
    private const val COMPRESSED_CHAT_MAX_BYTE_LENGTH = 255

    public const val TEMP_MOVE_BYTE_SIZE: Int = 1
    public const val FACE_DIRECTION_BYTE_SIZE: Int = 2
    public const val FACE_TARGET_BYTE_SIZE: Int = 2
    public const val ANIM_BYTE_SIZE: Int = 3
    public const val GFX_BYTE_SIZE: Int = 6
    public const val TINTING_BYTE_SIZE: Int = 8
    public const val EXACT_MOVE_BYTE_SIZE: Int = 10
    public const val APPEARANCE_MAX_BYTE_SIZE: Int =
        1 + /* 1 byte header for block data length */
            9 + APPEARANCE_LOOK_MAX_BYTE_LENGTH +
            APPEARANCE_COLORS_MAX_BYTE_LENGTH +
            APPEARANCE_BAS_MAX_BYTE_LENGTH +
            USERNAME_MAX_BYTE_LENGTH +
            (APPEARANCE_TAG_MAX_BYTE_LENGTH * 3)
    public const val NAME_PREFIX_BYTE_SIZE: Int = 72 /* arbitrary limit of 24 bytes per prefix string */
    public const val CHAT_MAX_BYTE_SIZE: Int = 7 + COMPRESSED_CHAT_MAX_BYTE_LENGTH
    public const val SAY_BYTE_SIZE: Int = 255 /* arbitrary limit */

    /* 2 byte header for hitmark and headbar count */
    public const val HIT_BYTE_SIZE: Int = 2 + (MAX_HIT_COUNT * PER_HIT_BYTE_LENGTH)

    public const val TOTAL_BYTE_SIZE: Int = TEMP_MOVE_BYTE_SIZE +
        FACE_DIRECTION_BYTE_SIZE +
        FACE_TARGET_BYTE_SIZE +
        ANIM_BYTE_SIZE +
        GFX_BYTE_SIZE +
        TINTING_BYTE_SIZE +
        EXACT_MOVE_BYTE_SIZE +
        APPEARANCE_LOOK_MAX_BYTE_LENGTH +
        NAME_PREFIX_BYTE_SIZE +
        CHAT_MAX_BYTE_SIZE +
        SAY_BYTE_SIZE +
        HIT_BYTE_SIZE
}
