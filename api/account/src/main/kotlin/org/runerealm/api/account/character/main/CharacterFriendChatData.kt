package org.runerealm.api.account.character.main

import org.runerealm.game.entity.player.FriendChatSettings

/** The JSON shape of a character's chat-channel setup (the `characters.friend_chat` column). */
public data class CharacterFriendChatData(
    val name: String? = null,
    val enterRank: Int = FriendChatSettings.DEFAULT.enterRank,
    val talkRank: Int = FriendChatSettings.DEFAULT.talkRank,
    val kickRank: Int = FriendChatSettings.DEFAULT.kickRank,
) {
    public fun toSettings(): FriendChatSettings =
        FriendChatSettings(
            name = name,
            enterRank = enterRank,
            talkRank = talkRank,
            kickRank = kickRank,
        )

    public companion object {
        public fun from(settings: FriendChatSettings): CharacterFriendChatData =
            CharacterFriendChatData(
                name = settings.name,
                enterRank = settings.enterRank,
                talkRank = settings.talkRank,
                kickRank = settings.kickRank,
            )
    }
}
