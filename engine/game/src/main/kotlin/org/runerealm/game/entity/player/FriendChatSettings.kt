package org.runerealm.game.entity.player

/**
 * A player's own friends chat ("chat-channel") setup.
 *
 * Ranks use the friends chat scale: `-1` anyone, `0` any friend, `1` Recruit up to `6` General, and
 * `7` the owner only.
 *
 * @property name the channel's name (its prefix), or `null` while the channel is disabled.
 */
public data class FriendChatSettings(
    val name: String?,
    val enterRank: Int,
    val talkRank: Int,
    val kickRank: Int,
) {
    public companion object {
        public const val RANK_ANYONE: Int = -1
        public const val RANK_FRIEND: Int = 0
        public const val RANK_RECRUIT: Int = 1
        public const val RANK_GENERAL: Int = 6
        public const val RANK_OWNER: Int = 7

        /** OSRS's defaults: disabled; anyone may enter and talk, and only the owner may kick. */
        public val DEFAULT: FriendChatSettings =
            FriendChatSettings(
                name = null,
                enterRank = RANK_ANYONE,
                talkRank = RANK_ANYONE,
                kickRank = RANK_OWNER,
            )
    }
}
