package org.runerealm.content.other.social

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.friendchat.MessageFriendChannel
import net.rsprot.protocol.game.outgoing.friendchat.UpdateFriendChatChannelFull
import net.rsprot.protocol.game.outgoing.friendchat.UpdateFriendChatChannelFullV2
import net.rsprot.protocol.game.outgoing.friendchat.UpdateFriendChatChannelSingleUser
import net.rsprot.protocol.game.outgoing.social.UpdateFriendList
import org.runerealm.api.player.events.social.FriendChatJoinLeave
import org.runerealm.api.player.events.social.FriendChatKick
import org.runerealm.api.player.events.social.FriendChatMessage
import org.runerealm.api.player.events.social.FriendChatSetRank
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.ui.ifOpenMain
import org.runerealm.api.player.ui.ifSetText
import org.runerealm.api.script.onEvent
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerLogout
import org.runerealm.api.server.config.ServerConfig
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.events.EventBus
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.entity.player.FriendChatSettings
import org.runerealm.game.entity.player.FriendChatSettings.Companion.RANK_ANYONE
import org.runerealm.game.entity.player.FriendChatSettings.Companion.RANK_FRIEND
import org.runerealm.game.entity.player.FriendChatSettings.Companion.RANK_OWNER
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Friends chat ("Chat-channel", formerly Clan Chat), following the OSRS Wiki
 * ([Chat-channel](https://oldschool.runescape.wiki/w/Chat-channel)):
 * - A player's channel only exists once they give it a name (its prefix, up to 12 characters) in
 *   the "Chat-channel Setup" interface, opened with the tab's Setup button. "Disable" removes it
 *   again.
 * - Join a channel by its owner's name, see who else is in it and talk to everyone in it by
 *   starting a line with "/".
 * - The owner ranks friends (Recruit to General) from the setup's friend list and picks who may
 *   enter, talk and kick. Players on the owner's ignore list cannot join.
 * - Kicked players are banned from the channel for 60 minutes, or until it empties.
 *
 * The owner must be online for their channel to be joined; a running channel stays up while anyone
 * is in it.
 */
class FriendChatScript
@Inject
constructor(
    private val players: PlayerList,
    private val config: ServerConfig,
    private val eventBus: EventBus,
    private val mapClock: MapClock,
) : PluginScript() {
    private val channels = mutableMapOf<String, Channel>()
    private var messageCounter = 0

    override fun ScriptContext.startup() {
        onEvent<FriendChatJoinLeave> {
            val name = name
            if (name.isNullOrBlank()) player.leaveChannel() else player.joinChannel(name)
        }
        onEvent<FriendChatMessage> { player.sendChannelMessage(text) }
        onEvent<FriendChatKick> { player.kick(name) }
        onEvent<FriendChatSetRank> { player.setFriendRank(name, rank) }
        onPlayerLogin { channels[player.displayName.lowercase()]?.owner = player }
        onPlayerLogout {
            player.leaveChannel(quiet = true)
            channels[player.displayName.lowercase()]?.let {
                if (it.owner === player) it.owner = null
            }
        }

        onIfOverlayButton(friendchat_components.current_setup) { player.openSetup() }
        onIfModalButton(friendchat_components.setup_prefix) { changePrefix(it.op) }
        onIfModalButton(friendchat_components.setup_enter) {
            player.updateSettings { copy(enterRank = it.op.slot - 2) }
        }
        onIfModalButton(friendchat_components.setup_talk) {
            player.updateSettings { copy(talkRank = it.op.slot - 2) }
        }
        onIfModalButton(friendchat_components.setup_kick) {
            player.updateSettings { copy(kickRank = it.op.slot - 2) }
        }
    }

    /* Setup */

    private fun Player.openSetup() {
        ifOpenMain(friendchat_interfaces.setup, eventBus)
        refreshSetupText()
    }

    private fun Player.refreshSetupText() {
        val settings = friendChat
        ifSetText(friendchat_components.setup_prefix, white(settings.name ?: "Chat disabled"))
        ifSetText(friendchat_components.setup_enter, white(rankRequirement(settings.enterRank)))
        ifSetText(friendchat_components.setup_talk, white(rankRequirement(settings.talkRank)))
        ifSetText(friendchat_components.setup_kick, white(rankRequirement(settings.kickRank)))
    }

    private suspend fun ProtectedAccess.changePrefix(op: IfButtonOp) {
        if (op == IfButtonOp.Op2) {
            player.updateSettings { copy(name = null) }
            return
        }
        val input = stringDialog("Enter chat prefix:").trim()
        if (input.isEmpty()) {
            return
        }
        if (input.length > MAX_NAME_LENGTH || !input.all { it.isLetterOrDigit() || it == ' ' }) {
            mes("Chat prefixes must be 1 to 12 letters, numbers or spaces.")
            return
        }
        player.updateSettings { copy(name = input) }
    }

    private fun Player.updateSettings(change: FriendChatSettings.() -> FriendChatSettings) {
        friendChat = friendChat.change()
        refreshSetupText()
        val channel = channels[displayName.lowercase()] ?: return
        if (friendChat.name == null) {
            for (member in channel.members.toList()) {
                member.leaveChannel(quiet = true)
                member.mes("The channel you were in has been disabled.")
            }
            channels.remove(channel.key)
            return
        }
        // Members who may no longer be in the channel are removed; everyone else is refreshed.
        for (member in channel.members.toList()) {
            if (!channel.mayEnter(member)) {
                member.leaveChannel(quiet = true)
                member.mes("You have been removed from this channel.")
            }
        }
        channel.sendFullToAll()
    }

    private fun Player.setFriendRank(input: String, rank: Int) {
        val name = friendList.firstOrNull { it.equals(input.trim(), ignoreCase = true) } ?: return
        val clamped = rank.coerceIn(RANK_FRIEND, FriendChatSettings.RANK_GENERAL)
        if (clamped == RANK_FRIEND) {
            friendChatRanks.remove(name.lowercase())
        } else {
            friendChatRanks[name.lowercase()] = clamped
        }
        val target = onlinePlayer(name)
        val entry =
            if (target != null && !target.ignoreList.containsName(displayName)) {
                UpdateFriendList.OnlineFriend(
                    false,
                    target.displayName,
                    null,
                    config.world,
                    clamped,
                    0,
                    "",
                    worldName,
                    0,
                    0,
                )
            } else {
                UpdateFriendList.OfflineFriend(false, name, null, clamped, 0, "")
            }
        client.write(UpdateFriendList(listOf(entry)))
        channels[displayName.lowercase()]?.sendFullToAll()
    }

    /* Joining and leaving */

    private fun Player.joinChannel(name: String) {
        leaveChannel(quiet = true)
        mes("Attempting to join chat-channel...")
        val key = name.trim().lowercase()
        val channel = channels[key] ?: openChannel(key)
        if (channel == null) {
            mes("The channel you tried to join does not exist.")
            return
        }
        val ban = channel.bans[displayName.lowercase()]
        if (ban != null && ban > mapClock.cycle) {
            mes("You are temporarily banned from this chat-channel.")
            return
        }
        if (channel.ignores(this)) {
            mes("You are not allowed to join this user's chat-channel.")
            return
        }
        if (!channel.mayEnter(this)) {
            mes("You do not have a high enough rank to join this chat-channel.")
            return
        }
        if (channel.members.size >= MAX_MEMBERS) {
            mes("The channel is full.")
            return
        }
        channel.members += this
        channels[channel.key] = channel
        client.write(UpdateFriendChatChannelFullV2(channel.joinUpdate()))
        val added =
            UpdateFriendChatChannelSingleUser.AddedFriendChatUser(
                displayName,
                config.world,
                channel.rankOf(this),
                worldName,
            )
        for (member in channel.members) {
            if (member !== this) {
                member.client.write(UpdateFriendChatChannelSingleUser(added))
            }
        }
        mes("Now talking in chat-channel ${channel.name}")
        mes("To talk, start each line of chat with the / symbol.")
    }

    /** Opens the channel of the online player [key] (a lowercase name), if they have set one up. */
    private fun openChannel(key: String): Channel? {
        val owner = players.firstOrNull { it.displayName.lowercase() == key } ?: return null
        if (owner.friendChat.name == null) {
            return null
        }
        return Channel(key, owner.displayName, owner)
    }

    private fun Player.leaveChannel(quiet: Boolean = false) {
        val channel = channels.values.firstOrNull { this in it.members }
        if (channel == null) {
            if (!quiet) {
                client.write(
                    UpdateFriendChatChannelFullV2(UpdateFriendChatChannelFullV2.LeaveUpdate)
                )
            }
            return
        }
        channel.members -= this
        client.write(UpdateFriendChatChannelFullV2(UpdateFriendChatChannelFullV2.LeaveUpdate))
        val removed =
            UpdateFriendChatChannelSingleUser.RemovedFriendChatUser(displayName, config.world)
        for (member in channel.members) {
            member.client.write(UpdateFriendChatChannelSingleUser(removed))
        }
        if (channel.members.isEmpty()) {
            // An empty channel resets, which also lifts its bans.
            channels.remove(channel.key)
        }
        if (!quiet) {
            mes("You have left the channel.")
        }
    }

    private fun Player.kick(input: String) {
        val channel = channels.values.firstOrNull { this in it.members } ?: return
        val target = channel.members.firstOrNull { it.displayName.equals(input, true) } ?: return
        val rank = channel.rankOf(this)
        if (rank < channel.settings.kickRank || channel.rankOf(target) >= rank) {
            mes("You are not allowed to kick in this chat-channel.")
            return
        }
        channel.bans[target.displayName.lowercase()] = mapClock.cycle + KICK_BAN_CYCLES
        target.leaveChannel(quiet = true)
        target.mes("You have been kicked from the channel.")
        for (member in channel.members) {
            member.mes("$displayName has kicked ${target.displayName} from the channel.")
        }
    }

    private fun Player.sendChannelMessage(text: String) {
        val channel = channels.values.firstOrNull { this in it.members }
        if (channel == null) {
            mes("You are not currently in a chat-channel.")
            return
        }
        if (!channel.mayTalk(this)) {
            mes("You do not have a high enough rank to talk in this chat-channel.")
            return
        }
        val message =
            MessageFriendChannel(displayName, channel.name, config.world, ++messageCounter, 0, text)
        for (member in channel.members) {
            member.client.write(message)
        }
    }

    private fun onlinePlayer(name: String): Player? =
        players.firstOrNull { it.displayName.equals(name, ignoreCase = true) }

    private val worldName: String
        get() = "World ${config.world}"

    private inner class Channel(val key: String, var ownerName: String, var owner: Player?) {
        val members = mutableListOf<Player>()
        val bans = mutableMapOf<String, Int>()

        /** The owner's latest setup, kept while they are logged out so the channel stays up. */
        private var lastSettings: FriendChatSettings =
            owner?.friendChat ?: FriendChatSettings.DEFAULT
        private var lastRanks: Map<String, Int> = owner?.friendChatRanks?.toMap() ?: emptyMap()
        private var lastFriends: List<String> = owner?.friendList?.toList() ?: emptyList()
        private var lastIgnores: List<String> = owner?.ignoreList?.toList() ?: emptyList()

        val settings: FriendChatSettings
            get() = owner?.friendChat?.also { snapshot() } ?: lastSettings

        val name: String
            get() = settings.name ?: ownerName

        private fun snapshot() {
            val owner = owner ?: return
            ownerName = owner.displayName
            lastSettings = owner.friendChat
            lastRanks = owner.friendChatRanks.toMap()
            lastFriends = owner.friendList.toList()
            lastIgnores = owner.ignoreList.toList()
        }

        fun rankOf(player: Player): Int {
            if (player.displayName.equals(ownerName, ignoreCase = true)) {
                return RANK_OWNER
            }
            settings // Refreshes the snapshot from an online owner.
            val name = player.displayName
            if (!lastFriends.containsName(name)) {
                return RANK_ANYONE
            }
            return lastRanks[name.lowercase()] ?: RANK_FRIEND
        }

        fun ignores(player: Player): Boolean {
            settings
            return lastIgnores.containsName(player.displayName)
        }

        fun mayEnter(player: Player): Boolean = rankOf(player) >= settings.enterRank

        fun mayTalk(player: Player): Boolean = rankOf(player) >= settings.talkRank

        fun joinUpdate(): UpdateFriendChatChannelFullV2.JoinUpdate =
            UpdateFriendChatChannelFullV2.JoinUpdate(
                ownerName,
                name,
                settings.kickRank,
                members.map { it.entry() },
            )

        fun sendFullToAll() {
            val update = UpdateFriendChatChannelFullV2(joinUpdate())
            for (member in members) {
                member.client.write(update)
            }
        }

        private fun Player.entry(): UpdateFriendChatChannelFull.FriendChatEntry =
            UpdateFriendChatChannelFull.FriendChatEntry(
                displayName,
                config.world,
                rankOf(this),
                worldName,
            )
    }

    private companion object {
        const val MAX_NAME_LENGTH = 12
        const val MAX_MEMBERS = 500

        /** 60 minutes of game cycles (0.6s each). */
        const val KICK_BAN_CYCLES = 6000

        fun List<String>.containsName(name: String): Boolean = any {
            it.equals(name, ignoreCase = true)
        }

        fun white(text: String): String = "<col=ffffff>$text</col>"

        fun rankRequirement(rank: Int): String =
            when (rank) {
                RANK_ANYONE -> "Anyone"
                RANK_FRIEND -> "Any friends"
                1 -> "Recruit+"
                2 -> "Corporal+"
                3 -> "Sergeant+"
                4 -> "Lieutenant+"
                5 -> "Captain+"
                6 -> "General+"
                else -> "Only me"
            }
    }
}

typealias friendchat_interfaces = FriendChatInterfaces

typealias friendchat_components = FriendChatComponents

object FriendChatInterfaces : InterfaceReferences() {
    val setup = find("chatchannel_setup")
}

object FriendChatComponents : ComponentReferences() {
    val current_setup = find("chatchannel_current:setup")
    val setup_prefix = find("chatchannel_setup:opt_prefix")
    val setup_enter = find("chatchannel_setup:opt_enter")
    val setup_talk = find("chatchannel_setup:opt_talk")
    val setup_kick = find("chatchannel_setup:opt_kick")
}
