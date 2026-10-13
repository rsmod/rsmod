package org.runerealm.content.other.social.clan

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.clan.ClanChannelFull
import net.rsprot.protocol.game.outgoing.clan.ClanSettingsFull
import net.rsprot.protocol.game.outgoing.clan.MessageClanChannel
import net.rsprot.protocol.game.outgoing.clan.VarClanDisable
import net.rsprot.protocol.game.outgoing.clan.VarClanEnable
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.events.social.ClanChatMessage
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.output.runClientScript
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.righthand
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.script.advanced.onOpPlayer5
import org.runerealm.api.script.onEvent
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpPlayerT
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerLogout
import org.runerealm.api.server.config.ServerConfig
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.other.social.showChannelsTab
import org.runerealm.content.other.social.showYourClanTab
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Clans, following the OSRS Wiki ([Clan](https://oldschool.runescape.wiki/w/Clan) and the
 * [clans guide](https://oldschool.runescape.wiki/w/Update:A_Guide_to_the_Clans_System)):
 * - Talk to the Clerk in the Clan Hub (south-east Grand Exchange) to form a clan. The clan creation
 *   side panel then replaces the "Your Clan" tab: name the clan (up to 12 characters, unique),
 *   invite founding members with Invite (a player target), and Confirm once five players (the
 *   founder and four others) are gathered. Cancel ends the formation (or leaves it, for a founding
 *   member).
 * - The founder becomes the owner and the founding members administrators. Clan members join the
 *   clan's chat channel on login, see it in the "Your Clan" tab and talk in it with "//"; the tab's
 *   Chat/Leave button joins or leaves the channel.
 * - The Clerk hands clan members a clan vexillum, whose "Recruit" option invites other players (who
 *   need Accept Aid on). A clan holds at most 500 members.
 * - The Clerk also lets members leave; an owner leaving hands the clan to the highest-ranked,
 *   longest-standing member, and a clan with no members left is disbanded.
 *
 * Clans are kept in [ClanRegistry] and saved through [ClanStore].
 *
 * Not implemented yet: the clan settings interfaces (`clans_info`, `clans_members`,
 * `clans_permissions`, rank titles, applicants, events, banned list - the Settings button only says
 * so), changing ranks or expelling members, guest clans ("View another clan" and "///" chat), clan
 * halls, coffers, clan cloaks, broadcasts, the 25-hours-played and once-a-day formation limits, and
 * the 7-day resignation countdown (an owner leaving hands over at once).
 */
class ClanScript
@Inject
constructor(
    private val players: PlayerList,
    private val config: ServerConfig,
    private val eventBus: EventBus,
    private val registry: ClanRegistry,
    private val store: ClanStore,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    private var messageCounter = (ClanRegistry.epochMinute() % 16_000) * 1_000

    override fun ScriptContext.startup() {
        store.loadAll { saved ->
            registry.load(saved)
            for (player in players) {
                player.syncClan()
            }
        }
        onPlayerLogin { player.syncClan() }
        onPlayerLogout { player.logoutClan() }
        onEvent<ClanChatMessage> { player.sendClanMessage(text, guest) }

        onOpNpc1(clan_npcs.clerk) { clerkDialogue(it.npc) }

        onIfOverlayButton(clan_components.creation_name) { player.launchNamePrompt() }
        onIfOverlayButton(clan_components.creation_cancel) { player.cancelFormation() }
        onIfOverlayButton(clan_components.creation_confirm) { player.confirmFormation() }
        onOpPlayerT(clan_components.creation_invite) { inviteFounder(it.target) }

        onIfOverlayButton(clan_components.sidepanel_chat) { player.toggleChannel() }
        onIfOverlayButton(clan_components.sidepanel_info) {
            player.mes("Clan settings can't be changed here yet.")
        }

        onOpPlayer5 { recruit(it.target) }
    }

    /* Login and the clan packets */

    private fun Player.syncClan() {
        val clan = registry.clanOf(this)
        if (clan == null) {
            clanAffinityStatus = if (registry.loaded) AFFINITY_NONE else AFFINITY_FETCHING
            return
        }
        val member = clan.member(characterId) ?: return
        if (member.displayName != displayName && displayName.isNotBlank()) {
            member.displayName = displayName
            store.updateMember(member)
        }
        sendClanSettings(clan)
        joinChannel(clan, quiet = true)
    }

    private fun Player.logoutClan() {
        registry.formationOf(this)?.let { leaveFormation(it) }
        val clan = registry.clanOf(this) ?: return
        if (clan.channel.remove(this)) {
            clan.updateNum++
            clan.sendChannelToAll()
        }
    }

    private fun Player.sendClanSettings(clan: Clan) {
        client.write(VarClanEnable)
        client.write(ClanSettingsFull(CLAN_TYPE, clan.settingsUpdate()))
        clanTransmitHomeworld = config.world
        clanAffinityStatus = AFFINITY_IN_CLAN
    }

    private fun Player.clearClan() {
        client.write(ClanChannelFull(CLAN_TYPE, ClanChannelFull.LeaveUpdate))
        client.write(ClanSettingsFull(CLAN_TYPE, ClanSettingsFull.LeaveUpdate))
        client.write(VarClanDisable)
        clanAffinityStatus = AFFINITY_NONE
    }

    private fun Clan.settingsUpdate(): ClanSettingsFull.JoinUpdate =
        ClanSettingsFull.JoinUpdate(
            updateNum = updateNum,
            creationTime = createdMinute,
            clanName = name,
            allowUnaffined = false,
            talkRank = talkRank,
            kickRank = kickRank,
            lootshareRank = 0,
            coinshareRank = 0,
            affinedMembers =
                members.map {
                    ClanSettingsFull.AffinedClanMember(
                        it.displayName,
                        it.rank,
                        0,
                        it.joinRuneDay,
                        false,
                    )
                },
            bannedMembers = emptyList(),
            settings = emptyList(),
        )

    private fun Clan.channelUpdate(): ClanChannelFull.JoinUpdate =
        ClanChannelFull.JoinUpdate(
            clanHash = id.toLong(),
            updateNum = updateNum.toLong(),
            clanName = name,
            discardedBoolean = false,
            kickRank = kickRank,
            talkRank = talkRank,
            members =
                channel.map { player ->
                    val rank = member(player.characterId)?.rank ?: ClanRanks.GUEST
                    ClanChannelFull.ClanMember(player.displayName, rank, config.world)
                },
        )

    private fun Clan.sendSettingsToAll() {
        val update = ClanSettingsFull(CLAN_TYPE, settingsUpdate())
        for (player in onlineMembers()) {
            player.client.write(update)
        }
    }

    private fun Clan.sendChannelToAll() {
        val update = ClanChannelFull(CLAN_TYPE, channelUpdate())
        for (player in channel) {
            player.client.write(update)
        }
    }

    private fun Clan.onlineMembers(): List<Player> =
        players.filter { member(it.characterId) != null }

    /* The clan chat channel */

    private fun Player.joinChannel(clan: Clan, quiet: Boolean = false) {
        if (this in clan.channel) {
            return
        }
        clan.channel += this
        clan.updateNum++
        clan.sendChannelToAll()
        if (!quiet) {
            mes("Now talking in your clan's chat channel.")
        }
        mes("To talk in your clan's channel, start each line of chat with // or /c.")
    }

    private fun Player.toggleChannel() {
        val clan = registry.clanOf(this) ?: return
        if (this in clan.channel) {
            clan.channel -= this
            clan.updateNum++
            client.write(ClanChannelFull(CLAN_TYPE, ClanChannelFull.LeaveUpdate))
            clan.sendChannelToAll()
            mes("You have left your clan's chat channel.")
        } else {
            joinChannel(clan)
        }
    }

    private fun Player.sendClanMessage(text: String, guest: Boolean) {
        if (guest) {
            mes("You are not currently a guest in a clan's chat channel.")
            return
        }
        val clan = registry.clanOf(this)
        if (clan == null) {
            mes("You are not in a clan.")
            return
        }
        if (this !in clan.channel) {
            mes("You are not currently chatting in your clan's channel.")
            return
        }
        val rank = clan.member(characterId)?.rank ?: ClanRanks.GUEST
        if (rank < clan.talkRank) {
            mes("You do not have a high enough rank to talk in your clan's channel.")
            return
        }
        messageCounter = (messageCounter + 1) and MESSAGE_COUNTER_MASK
        val message =
            MessageClanChannel(CLAN_TYPE, displayName, config.world, messageCounter, 0, text)
        for (member in clan.channel) {
            member.client.write(message)
        }
    }

    /* The Clerk */

    private suspend fun ProtectedAccess.clerkDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Good day.")
            val inClan = registry.clanOf(player) != null
            val option =
                if (inClan) {
                    choice4(
                        "What is a clan?",
                        ::whatIsAClan,
                        "I'm in a clan. Can you give me anything?",
                        ::giveClanItems,
                        "I'd like to leave my clan.",
                        ::leaveClanDialogue,
                        "Goodbye.",
                        ::goodbye,
                    )
                } else {
                    choice3(
                        "What is a clan?",
                        ::whatIsAClan,
                        "I'd like to form a clan.",
                        ::formClanDialogue,
                        "Goodbye.",
                        ::goodbye,
                    )
                }
            option(this)
        }

    private suspend fun whatIsAClan(dialogue: Dialogue) =
        with(dialogue) {
            chatPlayer(quiz, "What is a clan?")
            chatNpc(
                neutral,
                "Ah, clans. Where do I begin? Clans are groups of people gathered together for " +
                    "numerous reasons. Usually they have shared interests, like activities they " +
                    "enjoy together.",
            )
            chatNpc(
                neutral,
                "Should you choose to form your own clan you will need four friends to join you, " +
                    "and it is recommended that you have spent a good amount of time in this " +
                    "world first. Leadership is not for everyone.",
            )
        }

    private suspend fun goodbye(dialogue: Dialogue) =
        with(dialogue) {
            chatPlayer(neutral, "Goodbye.")
            chatNpc(neutral, "Goodbye. Come back any time.")
        }

    private suspend fun formClanDialogue(dialogue: Dialogue) =
        with(dialogue) {
            chatPlayer(neutral, "I'd like to form a clan.")
            if (!registry.loaded) {
                chatNpc(
                    sad,
                    "I'm afraid the clan records aren't available right now. Come back later.",
                )
                return
            }
            if (registry.formationOf(player) != null) {
                chatNpc(neutral, "You're already forming a clan. Use your clan side panel.")
                return
            }
            chatNpc(
                happy,
                "Splendid! Give your clan a name and invite four other players as founding " +
                    "members using the panel in your Your Clan tab. Confirm once you're ready.",
            )
            player.startFormation()
        }

    private suspend fun giveClanItems(dialogue: Dialogue) =
        with(dialogue) {
            chatPlayer(quiz, "I'm in a clan. Can you give me anything?")
            if (player.inv.isFull()) {
                chatNpc(
                    neutral,
                    "I've got cloaks that let you show your clan's colours, and vexilla to help " +
                        "you recruit more people into your clan, but you don't have space to take " +
                        "any.",
                )
                chatPlayer(neutral, "I suppose I'd better free up some space.")
                return
            }
            chatNpc(
                happy,
                "Certainly. I've got cloaks that let you show your clan's colours, and vexilla to " +
                    "help you recruit more people into your clan.",
            )
            access.invAdd(player.inv, clan_objs.vexillum)
            mesbox("The Clerk hands you a clan vexillum.")
        }

    private suspend fun leaveClanDialogue(dialogue: Dialogue) =
        with(dialogue) {
            chatPlayer(neutral, "I'd like to leave my clan.")
            val clan = registry.clanOf(player) ?: return
            val owner = clan.member(player.characterId)?.rank == ClanRanks.OWNER
            val title =
                if (owner && clan.members.size > 1) {
                    "Leave ${clan.name}? Ownership passes to your highest-ranked member."
                } else if (owner) {
                    "Leave ${clan.name}? It will be disbanded."
                } else {
                    "Leave ${clan.name}?"
                }
            val confirm = choice2("Yes, leave the clan.", true, "No.", false, title = title)
            if (confirm) {
                player.leaveClan(clan)
            }
        }

    /* Forming a clan */

    private fun Player.startFormation() {
        val formation = registry.startFormation(this)
        clanIAmFounder = 1
        showYourClanTab(eventBus, registry)
        formation.refreshFounderList()
    }

    private fun ClanFormation.refreshFounderList() {
        val names = members.map { it.displayName } + List(MAX_FOUNDERS) { "" }
        for (member in members) {
            member.runClientScript(
                SCRIPT_CREATE_LIST_PLAYERS,
                clan_components.creation_contents.packed,
                names[0],
                names[1],
                names[2],
                names[3],
                names[4],
                founder.displayName,
            )
        }
    }

    private fun Player.launchNamePrompt() {
        val formation = registry.formationOf(this) ?: return
        if (formation.founder !== this) {
            return
        }
        protectedAccess.launch(this) { promptClanName(formation) }
    }

    /** Asks the founder for a clan name; returns whether a valid, available name was chosen. */
    private suspend fun ProtectedAccess.promptClanName(formation: ClanFormation): Boolean {
        val name = stringDialog("Enter a name for your clan:").trim().replace(Regex(" +"), " ")
        if (registry.formationOf(player) !== formation) {
            return false
        }
        if (name.isEmpty() || name.length > MAX_NAME_LENGTH || !name.all(::isNameChar)) {
            mes("Clan names must be 1 to 12 letters, numbers or spaces.")
            return false
        }
        if (registry.isNameTaken(name) && !name.equals(formation.name, ignoreCase = true)) {
            mes("The name $name is not available.")
            return false
        }
        formation.name = name
        mes("The name $name is available.")
        return true
    }

    private suspend fun ProtectedAccess.inviteFounder(target: Player) {
        val formation = registry.formationOf(player) ?: return
        if (formation.founder !== player) {
            mes("Only the clan's founder can invite founding members.")
            return
        }
        if (target in formation.members) {
            mes("${target.displayName} is already a founding member.")
            return
        }
        if (formation.members.size >= MAX_FOUNDERS) {
            mes("Your clan already has enough founding members.")
            return
        }
        if (registry.clanOf(target) != null) {
            mes("${target.displayName} is already in a clan.")
            return
        }
        if (registry.formationOf(target) != null) {
            mes("${target.displayName} is already forming a clan.")
            return
        }
        val founder = player
        val asked =
            protectedAccess.launch(target) {
                val accept =
                    choice2(
                        "Yes, join the clan.",
                        true,
                        "No.",
                        false,
                        title = "Join ${founder.displayName}'s clan as a founding member?",
                    )
                if (!accept) {
                    founder.mes("${player.displayName} declined your invitation.")
                    return@launch
                }
                acceptFounderInvite(formation, player)
            }
        if (asked) {
            mes("Sending clan invitation to ${target.displayName}...")
        } else {
            mes("${target.displayName} is busy at the moment.")
        }
    }

    private fun acceptFounderInvite(formation: ClanFormation, player: Player) {
        val active = registry.formationOf(formation.founder) === formation
        if (!active || formation.members.size >= MAX_FOUNDERS || registry.clanOf(player) != null) {
            player.mes("That clan formation is no longer open.")
            return
        }
        if (registry.formationOf(player) != null) {
            return
        }
        formation.members += player
        player.clanIAmFounder = 0
        player.showYourClanTab(eventBus, registry)
        formation.refreshFounderList()
        formation.founder.mes("${player.displayName} has joined your clan as a founding member.")
        player.mes("You are now a founding member of ${formation.founder.displayName}'s clan.")
    }

    private fun Player.cancelFormation() {
        val formation = registry.formationOf(this) ?: return
        if (formation.founder === this) {
            registry.endFormation(formation)
            for (member in formation.members) {
                member.clanIAmFounder = 0
                member.showChannelsTab(eventBus, registry)
                member.mes("The clan formation has been cancelled.")
            }
        } else {
            leaveFormation(formation)
            mes("You have left the clan formation.")
        }
    }

    private fun Player.leaveFormation(formation: ClanFormation) {
        if (formation.founder === this) {
            registry.endFormation(formation)
            for (member in formation.members) {
                if (member !== this) {
                    member.showChannelsTab(eventBus, registry)
                    member.mes("The clan formation has been cancelled.")
                }
            }
            return
        }
        formation.members -= this
        showChannelsTab(eventBus, registry)
        formation.refreshFounderList()
        formation.founder.mes("$displayName has left your clan formation.")
    }

    private fun Player.confirmFormation() {
        val formation = registry.formationOf(this) ?: return
        if (formation.founder !== this) {
            return
        }
        if (formation.members.size < REQUIRED_FOUNDERS) {
            mes("You need $REQUIRED_FOUNDERS founding members, including yourself, to form a clan.")
            return
        }
        if (formation.name == null) {
            protectedAccess.launch(this) {
                if (promptClanName(formation)) {
                    player.formClan(formation)
                }
            }
            return
        }
        formClan(formation)
    }

    private fun Player.formClan(formation: ClanFormation) {
        val name = formation.name ?: return
        registry.endFormation(formation)
        if (registry.isNameTaken(name)) {
            formation.name = null
            registry.startFormation(this).apply {
                members.clear()
                members += formation.members
            }
            mes("The name $name is no longer available. Please choose another.")
            return
        }
        val runeDay = ClanRegistry.runeDay()
        val clan = Clan(registry.nextId(), name, ClanRegistry.epochMinute())
        for (member in formation.members) {
            val rank = if (member === this) ClanRanks.OWNER else ClanRanks.ADMINISTRATOR
            clan.members += ClanMember(member.characterId, member.displayName, rank, runeDay)
        }
        registry.add(clan)
        store.insertClan(clan)
        for (member in formation.members) {
            member.clanIAmFounder = 0
            member.showChannelsTab(eventBus, registry)
            member.sendClanSettings(clan)
            member.joinChannel(clan, quiet = true)
            member.mes("Congratulations! The clan ${clan.name} has been formed.")
        }
    }

    /* Recruiting with a clan vexillum */

    private suspend fun ProtectedAccess.recruit(target: Player) {
        val vexillum = player.righthand?.id
        if (vexillum == null || clan_objs.vexilla.none { it.id == vexillum }) {
            return
        }
        val clan = registry.clanOf(player)
        if (clan == null) {
            mes("You are not in a clan.")
            return
        }
        val rank = clan.member(player.characterId)?.rank ?: ClanRanks.GUEST
        if (rank < RECRUIT_RANK) {
            mes("You do not have a high enough rank to recruit members.")
            return
        }
        if (registry.clanOf(target) != null) {
            mes("${target.displayName} is already in a clan.")
            return
        }
        if (target.acceptAid == 0) {
            mes("${target.displayName} is not accepting aid.")
            return
        }
        if (clan.members.size >= MAX_MEMBERS) {
            mes("Your clan is full.")
            return
        }
        val recruiter = player
        val asked =
            protectedAccess.launch(target) {
                val accept =
                    choice2(
                        "Yes, join ${clan.name}.",
                        true,
                        "No.",
                        false,
                        title = "${recruiter.displayName} has invited you to join ${clan.name}.",
                    )
                if (!accept) {
                    recruiter.mes("${player.displayName} declined your invitation.")
                    return@launch
                }
                player.joinClan(clan, recruiter)
            }
        if (asked) {
            mes("Sending clan invitation to ${target.displayName}...")
        } else {
            mes("${target.displayName} is busy at the moment.")
        }
    }

    private fun Player.joinClan(clan: Clan, recruiter: Player) {
        if (registry.clanOf(this) != null || clan.members.size >= MAX_MEMBERS) {
            return
        }
        if (registry.all.none { it === clan }) {
            mes("That clan no longer exists.")
            return
        }
        registry.formationOf(this)?.let { leaveFormation(it) }
        val member = ClanMember(characterId, displayName, ClanRanks.RECRUIT, ClanRegistry.runeDay())
        clan.members += member
        clan.updateNum++
        store.insertMember(clan, member)
        clan.sendSettingsToAll()
        sendClanSettings(clan)
        joinChannel(clan, quiet = true)
        showYourClanTab(eventBus, registry)
        mes("You are now a member of ${clan.name}.")
        recruiter.mes("$displayName has joined your clan.")
    }

    /* Leaving */

    private fun Player.leaveClan(clan: Clan) {
        val member = clan.member(characterId) ?: return
        clan.members -= member
        clan.channel -= this
        clan.updateNum++
        clearClan()
        if (clan.members.isEmpty()) {
            registry.remove(clan)
            store.deleteClan(clan)
            mes("You have left ${clan.name}. With no members left, the clan has been disbanded.")
            return
        }
        store.deleteMember(member)
        if (member.rank == ClanRanks.OWNER) {
            // The wiki's resignation hands the clan to the highest-ranked, longest-standing member.
            val heir = clan.members.maxBy { it.rank }
            heir.rank = ClanRanks.OWNER
            store.updateMember(heir)
            players
                .firstOrNull { it.characterId == heir.characterId }
                ?.mes("You are now the owner of ${clan.name}.")
        }
        clan.sendSettingsToAll()
        clan.sendChannelToAll()
        mes("You have left ${clan.name}.")
    }

    private companion object {
        const val CLAN_TYPE = 0
        const val AFFINITY_FETCHING = 2
        const val AFFINITY_NONE = 1
        const val AFFINITY_IN_CLAN = 3

        /** The wiki: "Four other players (five total) must form a clan as founding members." */
        const val REQUIRED_FOUNDERS = 5

        /** The creation panel lists five founding members. */
        const val MAX_FOUNDERS = 5

        const val MAX_MEMBERS = 500
        const val MAX_NAME_LENGTH = 12
        const val RECRUIT_RANK = ClanRanks.ADMINISTRATOR
        const val MESSAGE_COUNTER_MASK = 0xFFFFFF

        /** `[clientscript,clan_create_side_list_players]`. */
        const val SCRIPT_CREATE_LIST_PLAYERS = 4448

        fun isNameChar(char: Char): Boolean = char.isLetterOrDigit() || char == ' '
    }
}

private var Player.clanAffinityStatus by intVarBit(clan_varbits.affinity_status)
private var Player.clanIAmFounder by intVarBit(clan_varbits.i_am_founder)
private var Player.clanTransmitHomeworld by intVarBit(clan_varbits.transmit_homeworld)
private val Player.acceptAid by intVarBit(clan_varbits.accept_aid)

typealias clan_components = ClanComponents

typealias clan_varbits = ClanVarBits

typealias clan_npcs = ClanNpcs

typealias clan_objs = ClanObjs

object ClanComponents : ComponentReferences() {
    val creation_contents = find("clans_creation_sidepanel:contents")
    val creation_name = find("clans_creation_sidepanel:name")
    val creation_invite = find("clans_creation_sidepanel:invite")
    val creation_cancel = find("clans_creation_sidepanel:cancel")
    val creation_confirm = find("clans_creation_sidepanel:confirm")
    val sidepanel_chat = find("clans_sidepanel:chat")
    val sidepanel_info = find("clans_sidepanel:info")
}

object ClanVarBits : VarBitReferences() {
    val affinity_status = find("clan_affinitystatus")
    val i_am_founder = find("clan_i_am_founder")
    val transmit_homeworld = find("clan_transmit_homeworld")
    val accept_aid = find("option_acceptaid")
}

object ClanNpcs : NpcReferences() {
    val clerk = find("clan_clerk")
}

object ClanObjs : ObjReferences() {
    val vexillum = find("clan_vexillum_black")
    val vexillum_orange = find("clan_vexillum_orange")
    val vexillum_blue = find("clan_vexillum_blue")
    val vexillum_red = find("clan_vexillum_red")
    val vexillum_green = find("clan_vexillum_green")
    val vexillum_yellow = find("clan_vexillum_yellow")
    val vexillum_lightblue = find("clan_vexillum_lightblue")
    val vexillum_pink = find("clan_vexillum_pink")

    val vexilla =
        listOf(
            vexillum,
            vexillum_orange,
            vexillum_blue,
            vexillum_red,
            vexillum_green,
            vexillum_yellow,
            vexillum_lightblue,
            vexillum_pink,
        )
}
