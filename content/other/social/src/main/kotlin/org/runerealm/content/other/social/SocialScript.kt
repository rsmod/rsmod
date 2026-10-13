package org.runerealm.content.other.social

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.social.UpdateFriendList
import net.rsprot.protocol.game.outgoing.social.UpdateIgnoreList
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.events.social.SocialEvents
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.ui.ifOpenOverlay
import org.runerealm.api.script.onEvent
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerLogout
import org.runerealm.api.server.config.ServerConfig
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Friend and ignore lists, following the OSRS Wiki ([Friends
 * List](https://oldschool.runescape.wiki/w/Friends_List),
 * [Ignore List](https://oldschool.runescape.wiki/w/Ignore_List)):
 * - The friend list holds 400 names (the wiki's members limit: there is no separate free-player
 *   limit here) and the ignore list holds 400 names.
 * - Friends show as online, with their world, while they are logged in, unless they ignore you.
 *
 * Names reach the server through the client's `FRIENDLIST_ADD`/`IGNORELIST_ADD` packets, whether
 * typed after pressing the tab's add button or chosen from a right-click "Add friend"/"Add ignore"
 * option on a player or a chat line.
 */
class SocialScript
@Inject
constructor(
    private val players: PlayerList,
    private val config: ServerConfig,
    private val eventBus: EventBus,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerLogin {
            player.sendLists()
            player.notifyFriends(online = true)
        }
        onPlayerLogout { player.notifyFriends(online = false) }
        onEvent<SocialEvents.FriendAdd> { player.addFriend(name) }
        onEvent<SocialEvents.FriendDel> { player.removeFriend(name) }
        onEvent<SocialEvents.IgnoreAdd> { player.addIgnore(name) }
        onEvent<SocialEvents.IgnoreDel> { player.removeIgnore(name) }
        onIfOverlayButton(social_components.view_ignore) {
            player.ifOpenOverlay(
                social_interfaces.ignore,
                components.toplevel_target_side9,
                eventBus,
            )
        }
        onIfOverlayButton(social_components.view_friends) {
            player.ifOpenOverlay(interfaces.friends, components.toplevel_target_side9, eventBus)
        }
    }

    /**
     * Sends both lists on login. The client keeps showing "Loading friends list" on the friends and
     * ignore tabs - and ignores the add buttons - until it receives an `UPDATE_FRIENDLIST` packet:
     * `FRIENDLIST_LOADED` alone only moves it to its intermediate "connecting" state. The friend
     * list is therefore always sent, even when it is empty.
     */
    private fun Player.sendLists() {
        client.write(UpdateIgnoreList(ignoreList.map { ignoreEntry(it, added = false) }))
        client.write(UpdateFriendList(friendList.map { friendEntry(it, added = false) }))
    }

    private fun Player.addFriend(input: String) {
        val name = input.trim()
        if (!isValidName(name)) {
            return
        }
        if (name.equals(displayName, ignoreCase = true)) {
            mes("You can't add yourself to your own friend list.")
            return
        }
        if (friendList.size >= FRIEND_LIMIT) {
            mes("Your friend list is full. Max of 200 for free users, and 400 for members.")
            return
        }
        if (friendList.containsName(name)) {
            mes("$name is already on your friend list.")
            return
        }
        if (ignoreList.containsName(name)) {
            mes("Please remove $name from your ignore list first.")
            return
        }
        val stored = onlinePlayer(name)?.displayName ?: name
        friendList += stored
        client.write(UpdateFriendList(listOf(friendEntry(stored, added = true))))
    }

    private fun Player.removeFriend(input: String) {
        val name = input.trim()
        friendList.removeAll { it.equals(name, ignoreCase = true) }
        friendChatRanks.remove(name.lowercase())
    }

    private fun Player.addIgnore(input: String) {
        val name = input.trim()
        if (!isValidName(name)) {
            return
        }
        if (name.equals(displayName, ignoreCase = true)) {
            mes("You can't add yourself to your own ignore list.")
            return
        }
        if (ignoreList.size >= IGNORE_LIMIT) {
            mes("Your ignore list is full. Max of 400 users.")
            return
        }
        if (ignoreList.containsName(name)) {
            mes("$name is already on your ignore list.")
            return
        }
        if (friendList.containsName(name)) {
            mes("Please remove $name from your friend list first.")
            return
        }
        val target = onlinePlayer(name)
        val stored = target?.displayName ?: name
        ignoreList += stored
        client.write(UpdateIgnoreList(listOf(ignoreEntry(stored, added = true))))
        // Someone you ignore sees you as offline on their friend list.
        if (target != null && target.friendList.containsName(displayName)) {
            target.client.write(UpdateFriendList(listOf(target.offlineEntry(displayName))))
        }
    }

    private fun Player.removeIgnore(input: String) {
        val name = input.trim()
        val removed = ignoreList.firstOrNull { it.equals(name, ignoreCase = true) } ?: return
        ignoreList.remove(removed)
        client.write(UpdateIgnoreList(listOf(UpdateIgnoreList.RemovedIgnoredEntry(removed))))
        val target = onlinePlayer(removed)
        if (target != null && target.friendList.containsName(displayName)) {
            target.client.write(UpdateFriendList(listOf(target.friendEntry(displayName, false))))
        }
    }

    /** Tells every online player with this player on their friend list that it logged in/out. */
    private fun Player.notifyFriends(online: Boolean) {
        val name = displayName
        if (name.isBlank()) {
            return
        }
        for (other in players) {
            if (other === this || !other.friendList.containsName(name)) {
                continue
            }
            if (online && ignoreList.containsName(other.displayName)) {
                continue
            }
            val entry =
                if (online) {
                    other.onlineEntry(name, added = false)
                } else {
                    other.offlineEntry(name)
                }
            other.client.write(UpdateFriendList(listOf(entry)))
        }
    }

    private fun Player.friendEntry(name: String, added: Boolean): UpdateFriendList.Friend {
        val target = onlinePlayer(name)
        // A friend who ignores this player appears offline to them.
        if (target != null && !target.ignoreList.containsName(displayName)) {
            return onlineEntry(target.displayName, added)
        }
        return offlineEntry(name, added)
    }

    private fun Player.onlineEntry(name: String, added: Boolean): UpdateFriendList.OnlineFriend =
        UpdateFriendList.OnlineFriend(
            added,
            name,
            null,
            config.world,
            friendChatRank(name),
            0,
            "",
            "World ${config.world}",
            0,
            0,
        )

    private fun Player.offlineEntry(
        name: String,
        added: Boolean = false,
    ): UpdateFriendList.OfflineFriend =
        UpdateFriendList.OfflineFriend(added, name, null, friendChatRank(name), 0, "")

    private fun ignoreEntry(name: String, added: Boolean): UpdateIgnoreList.IgnoredPlayer =
        UpdateIgnoreList.AddedIgnoredEntry(name, null, "", added)

    private fun onlinePlayer(name: String): Player? =
        players.firstOrNull { it.displayName.equals(name, ignoreCase = true) }

    private fun List<String>.containsName(name: String): Boolean = any {
        it.equals(name, ignoreCase = true)
    }

    private fun isValidName(name: String): Boolean = name.length in 1..MAX_NAME_LENGTH

    private companion object {
        const val FRIEND_LIMIT = 400
        const val IGNORE_LIMIT = 400
        const val MAX_NAME_LENGTH = 12
    }
}

/** The friends chat rank (0 = plain friend, 1 Recruit .. 6 General) this player gave [name]. */
internal fun Player.friendChatRank(name: String): Int = friendChatRanks[name.lowercase()] ?: 0

typealias social_interfaces = SocialInterfaces

typealias social_components = SocialComponents

object SocialInterfaces : InterfaceReferences() {
    val ignore = find("ignore")
}

object SocialComponents : ComponentReferences() {
    val view_ignore = find("friends:ignore")
    val view_friends = find("ignore:friends")
}
