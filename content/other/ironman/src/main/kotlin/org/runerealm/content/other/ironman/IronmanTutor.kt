package org.runerealm.content.other.ironman

import jakarta.inject.Inject
import org.runerealm.api.invtx.invAdd
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.ironman.IronmanMode
import org.runerealm.api.player.ironman.ironmanMode
import org.runerealm.api.player.ironman.isIronman
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.script.onOpNpc4
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.content.other.ironman.configs.ironman_components
import org.runerealm.content.other.ironman.configs.ironman_interfaces
import org.runerealm.content.other.ironman.configs.ironman_npcs
import org.runerealm.content.other.ironman.configs.ironman_objs
import org.runerealm.content.other.ironman.configs.ironman_varps
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Adam, the Ironman tutor in Lumbridge. Dialogue follows
 * [Transcript:Ironman tutor](https://oldschool.runescape.wiki/w/Transcript:Ironman_tutor), using
 * "Ironman" for every player since the 15 May 2024 wording change.
 *
 * While an account is still fresh (see [IronmanDowngrades.isFreshAccount]) Adam lets the player
 * choose any mode, as Paul does on Tutorial Island; otherwise only downgrades are offered.
 */
class IronmanTutor @Inject constructor(private val downgrades: IronmanDowngrades) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerLogin { downgrades.applyDueDowngrade(player) }
        onOpNpc1(ironman_npcs.adam) { talkToAdam(it.npc) }
        onOpNpc3(ironman_npcs.adam) { startDialogue(it.npc) { giveArmour() } }
        onOpNpc4(ironman_npcs.adam) { setup(it.npc) }

        onIfModalButton(ironman_components.none_button) { selectMode(IronmanMode.None) }
        onIfModalButton(ironman_components.im_button) { selectMode(IronmanMode.Standard) }
        onIfModalButton(ironman_components.uim_button) { selectMode(IronmanMode.Ultimate) }
        onIfModalButton(ironman_components.hcim_button) { selectMode(IronmanMode.Hardcore) }
        onIfModalButton(ironman_components.gim_button) { groupModeUnavailable() }
        onIfModalButton(ironman_components.hcgim_button) { groupModeUnavailable() }
        onIfModalButton(ironman_components.ugim_button) { groupModeUnavailable() }
        onIfModalButton(ironman_components.downgrade_short) { selectShortProtection() }
        onIfModalButton(ironman_components.downgrade_long) { selectLongProtection() }
        onIfModalButton(ironman_components.downgrade_cancel) { cancelDowngrade() }
    }

    private suspend fun ProtectedAccess.talkToAdam(npc: Npc) =
        startDialogue(npc) {
            val mode = player.ironmanMode
            if (mode.isIronman) {
                chatPlayer(neutral, "Hey there Adam.")
                chatNpc(happy, "Hail, ${mode.displayName}! What can I do for you?")
            } else {
                chatNpc(
                    happy,
                    "Hello, ${player.displayName}. I'm the Ironman tutor. What can I do for you?",
                )
            }
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice4(
                "Tell me about Ironmen.",
                1,
                "I'd like to review my Ironman mode.",
                2,
                "Have you any armour for me, please?",
                3,
                "I'm fine, thanks.",
                4,
            )
        when (choice) {
            1 -> tellMeAboutIronmen()
            2 -> reviewMode()
            3 -> requestArmour()
            4 -> chatPlayer(neutral, "I'm fine, thanks.")
        }
    }

    private suspend fun Dialogue.tellMeAboutIronmen() {
        chatPlayer(quiz, "Tell me about Ironmen.")
        chatNpc(
            neutral,
            "When you play as an <col=7f0000>Ironman</col>, you do everything for yourself. " +
                "You don't trade with other players, or take their items, or accept their help.",
        )
        chatNpc(
            neutral,
            "As an <col=7f0000>Ironman</col>, you choose to have these restrictions imposed on " +
                "you, so everyone knows you're doing it properly.",
        )
        chatNpc(
            neutral,
            "If you think you have what it takes, you can choose to become a " +
                "<col=b10000>Hardcore Ironman</col>.",
        )
        chatNpc(
            neutral,
            "In addition to the standard restrictions, <col=b10000>Hardcore Ironmen</col> only " +
                "have <col=b10000>one life</col>. In the event of a dangerous death, your " +
                "<col=b10000>Hardcore Ironman</col> status will be downgraded to that of a " +
                "<col=7f0000>standard Ironman</col>, and your",
        )
        chatNpc(neutral, "stats will be frozen on the <col=b10000>Hardcore Ironman</col> hiscores.")
        val ironman = player.isIronman
        if (!ironman) {
            chatNpc(
                neutral,
                "If you have some friends, you could stand alone... Together, as " +
                    "<col=7f0000>Group Ironmen</col>. Group Ironmen have the same restrictions " +
                    "as regular Ironmen except you can trade your items with each other!",
            )
            chatNpc(
                neutral,
                "If you are a daring bunch, you could even try <col=b10000>Hardcore Group " +
                    "Ironmen</col>. With Hardcore Group Ironmen you have a set number of lives, " +
                    "when they reach zero, you will become regular <col=7f0000>Group Ironmen</col>.",
            )
            chatNpc(
                neutral,
                "If you ever decided to invite experienced players to your group, you would " +
                    "become <col=7f0000>Unranked Group Ironmen</col>, which are no longer shown " +
                    "on the Group Ironman hiscores.",
            )
        }
        chatNpc(
            neutral,
            "For the ultimate challenge, you can choose to become an <col=00007f>Ultimate " +
                "Ironman</col>, a game mode inspired by the player <col=00007f>IronNoBank</col>.",
        )
        chatNpc(
            neutral,
            "In addition to the standard restrictions, <col=00007f>Ultimate Ironmen</col> are " +
                "blocked from using the bank, and they drop all their items when they die.",
        )
        when {
            player.ironmanMode == IronmanMode.Standard ->
                chatNpc(
                    neutral,
                    "You're a <col=7f0000>standard Ironman</col>. You can downgrade yourself to " +
                        "a normal player if you like.",
                )
            !ironman && downgrades.isFreshAccount(player) ->
                // Paul's closing line on Tutorial Island; fresh accounts may still choose a mode.
                chatNpc(
                    neutral,
                    "I must warn you that Ironman mode is not recommended for new players. You " +
                        "can still give it a go if you think you're up for the challenge though.",
                )
            !ironman ->
                chatNpc(
                    neutral,
                    "You can choose to become an <col=7f0000>Ironman</col> while you're on " +
                        "Tutorial Island. Although you can't do that on this account, you might " +
                        "like to bear it in mind for the future.",
                )
        }
        val choice =
            choice3(
                "I'd like to change my Ironman mode.",
                1,
                "Have you any armour for me, please?",
                2,
                "I'm fine, thanks.",
                3,
            )
        when (choice) {
            1 -> changeMode()
            2 -> requestArmour()
            3 -> chatPlayer(neutral, "I'm fine, thanks.")
        }
    }

    private suspend fun Dialogue.reviewMode() {
        chatPlayer(neutral, "I'd like to review my Ironman mode.")
        if (player.isIronman || downgrades.isFreshAccount(player)) {
            access.openSetup()
        }
    }

    private suspend fun Dialogue.changeMode() {
        chatPlayer(neutral, "I'd like to change my Ironman mode.")
        if (player.isIronman || downgrades.isFreshAccount(player)) {
            access.openSetup()
        }
    }

    private suspend fun Dialogue.requestArmour() {
        chatPlayer(quiz, "Have you any armour for me, please?")
        giveArmour()
    }

    /** The "Armour" right-click option skips straight to handing out the armour. */
    private suspend fun Dialogue.giveArmour() {
        val armour = player.ironmanMode.armour()
        if (armour == null) {
            chatNpc(neutral, "You're not an Ironman. This armour is only for them.")
            return
        }
        if (player.inv.freeSpace() < armour.size) {
            access.mes("You don't have enough inventory space.")
            return
        }
        for (obj in armour) {
            player.invAdd(player.inv, obj)
        }
        chatNpc(happy, "There you go. Wear it with pride.")
    }

    private fun IronmanMode.armour(): List<ObjType>? =
        when (this) {
            IronmanMode.Standard ->
                listOf(
                    ironman_objs.ironman_helm,
                    ironman_objs.ironman_platebody,
                    ironman_objs.ironman_platelegs,
                )
            IronmanMode.Ultimate ->
                listOf(
                    ironman_objs.ultimate_ironman_helm,
                    ironman_objs.ultimate_ironman_platebody,
                    ironman_objs.ultimate_ironman_platelegs,
                )
            IronmanMode.Hardcore ->
                listOf(
                    ironman_objs.hardcore_ironman_helm,
                    ironman_objs.hardcore_ironman_platebody,
                    ironman_objs.hardcore_ironman_platelegs,
                )
            else -> null
        }

    /** The "Setup" right-click option opens the Ironman setup interface directly. */
    private suspend fun ProtectedAccess.setup(npc: Npc) {
        if (player.isIronman || downgrades.isFreshAccount(player)) {
            openSetup()
            return
        }
        startDialogue(npc) {
            chatNpc(
                neutral,
                "You can choose to become an <col=7f0000>Ironman</col> while you're on " +
                    "Tutorial Island. Although you can't do that on this account, you might " +
                    "like to bear it in mind for the future.",
            )
        }
    }

    private fun ProtectedAccess.openSetup() {
        player.refreshSetupVars()
        ifOpenMainModal(ironman_interfaces.ironman_setup)
    }

    private fun Player.refreshSetupVars() = refreshSetupVars(downgrades)

    private fun ProtectedAccess.selectMode(mode: IronmanMode) {
        val current = player.ironmanMode
        if (mode == current) {
            return
        }
        if (downgrades.isFreshAccount(player)) {
            downgrades.setMode(player, mode)
        } else if (mode in downgrades.allowedDowngrades(current)) {
            if (downgrades.hasPendingDowngrade(player)) {
                return
            }
            downgrades.scheduleDowngrade(player, mode)
        }
        player.refreshSetupVars()
    }

    private fun ProtectedAccess.groupModeUnavailable() {
        mes("Group Ironman modes are not available on this server.")
    }

    private fun ProtectedAccess.selectShortProtection() {
        downgrades.setShortProtection(player)
    }

    private fun ProtectedAccess.selectLongProtection() {
        if (downgrades.totalLevel(player) < IronmanDowngrades.LONG_PROTECTION_TOTAL_LEVEL) {
            mes("You need a total level of at least 1,000 to select this option.")
            return
        }
        downgrades.setLongProtection(player)
    }

    private fun ProtectedAccess.cancelDowngrade() {
        downgrades.cancelDowngrade(player)
        player.refreshSetupVars()
    }
}

/** Updates the vars the `ironman_setup` interface reads (free choice, protection, countdown). */
internal fun Player.refreshSetupVars(downgrades: IronmanDowngrades) {
    setupUnavailable = 0
    freeChoice = if (downgrades.isFreshAccount(this)) 1 else 0
    longProtectionAvailable = 1
    daysRemaining = downgrades.daysRemaining(this)
}

private var Player.daysRemaining by intVarp(ironman_varps.days_remaining)
private var Player.longProtectionAvailable by intVarp(ironman_varps.long_protection_available)
private var Player.setupUnavailable by intVarp(ironman_varps.setup_unavailable)
private var Player.freeChoice by intVarp(ironman_varps.free_choice)
