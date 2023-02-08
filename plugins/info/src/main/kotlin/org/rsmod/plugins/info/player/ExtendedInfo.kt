package org.rsmod.plugins.info.player

public class ExtendedInfo {

    public var tempMove: ByteArray = ByteArray(TEMP_MOVE_BYTE_SIZE)
    public var faceDir: ByteArray = ByteArray(FACE_DIRECTION_BYTE_SIZE)
    public var faceTarget: ByteArray = ByteArray(FACE_TARGET_BYTE_SIZE)
    public var anim: ByteArray = ByteArray(ANIM_BYTE_SIZE)
    public var gfx: ByteArray = ByteArray(GFX_BYTE_SIZE)
    public var tint: ByteArray = ByteArray(TINTING_BYTE_SIZE)
    public var exactMove: ByteArray = ByteArray(EXACT_MOVE_BYTE_SIZE)
    public val appearance: ByteArray = ByteArray(APPEARANCE_MAX_BYTE_SIZE)
    public var namePrefix: ByteArray = ByteArray(NAME_PREFIX_BYTE_SIZE)
    public var manualChat: ByteArray = ByteArray(CHAT_MAX_BYTE_SIZE)
    public var forcedChat: ByteArray = ByteArray(SAY_BYTE_SIZE)
    public var hits: ByteArray = ByteArray(HIT_BYTE_SIZE)

    public companion object {

        private const val USERNAME_MAX_BYTE_LENGTH = 13 /* max username string length + 1 (to write size) */
        private const val MAX_HIT_COUNT = 255 /* max amount of hits that can be sent to client */
        private const val PER_HIT_BYTE_LENGTH = 19 + 1 /* 19 bytes per hit + 1 byte for custom source pid */
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
            1 + /* custom 1 byte header for non-transformed byte length */
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
    }
}
