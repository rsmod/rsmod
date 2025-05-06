package org.rsmod.content.other.commands

import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.db.DatabaseConnection
import org.rsmod.api.db.gateway.GameDbManager
import org.rsmod.api.db.gateway.model.GameDbResult
import org.rsmod.api.db.gateway.model.isErr
import org.rsmod.api.db.util.setNullableString
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.realm.Realm
import org.rsmod.api.realm.RealmConfig
import org.rsmod.api.realm.config.updater.RealmConfigUpdater
import org.rsmod.game.cheat.Cheat
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RealmConfigCommands
@Inject
constructor(
    private val realm: Realm,
    private val realmUpdater: RealmConfigUpdater,
    private val protectedAccess: ProtectedAccessLauncher,
    private val playerList: PlayerList,
    private val db: GameDbManager,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onCommand("realm", "Update realm configs", ::updateRealm)
    }

    private fun updateRealm(cheat: Cheat) =
        with(cheat) {
            val launched = protectedAccess.launch(player) { promptConfigSelection() }
            if (!launched) {
                player.mes(constants.dm_busy)
            }
        }

    private suspend fun ProtectedAccess.promptConfigSelection() {
        var current = realm.config
        while (true) {
            val selectionLines =
                selections.map { it.formatText(current) } + listOf("Revert", "Confirm")
            val selectionIndex =
                menu(
                    title = "Realm configs: '${realm.name}'",
                    hotkeys = false,
                    choices = selectionLines,
                )

            val selectRevert = selectionIndex == selectionLines.lastIndex - 1
            if (selectRevert) {
                val revert = confirmRevert()
                if (revert) {
                    current = realm.config
                }
                continue
            }

            val selectConfirm = selectionIndex == selectionLines.lastIndex
            if (selectConfirm) {
                val confirm = confirmChanges(active = realm.config, update = current)
                val noChanges = confirm == null
                val applyChanges = confirm == true
                when {
                    noChanges -> return
                    applyChanges -> break
                    else -> continue
                }
            }

            val selected = selections[selectionIndex]
            current = with(selected) { onSelect(current) }
        }
        applyChanges(player, current)
    }

    private suspend fun ProtectedAccess.confirmRevert(): Boolean {
        val confirm =
            choice2(
                "Yes.",
                true,
                "No.",
                false,
                title = "Are you sure you wish to revert all changes?",
            )
        return confirm
    }

    private suspend fun ProtectedAccess.confirmChanges(
        active: RealmConfig,
        update: RealmConfig,
    ): Boolean? {
        if (update == active) {
            mes("No changes have been detected.")
            return null
        }
        val confirm =
            choice2(
                "Yes, apply changes.",
                true,
                "No, go back.",
                false,
                title = "Are you sure you wish to apply all changes?",
            )
        return confirm
    }

    private fun applyChanges(player: Player, updated: RealmConfig) {
        val uid = player.uid
        db.request(request = { applyChanges(updated, it) }, response = { applyChanges(uid, it) })
        player.mes("Changes have been submitted. This may take a few seconds to take effect...")
    }

    private fun applyChanges(
        config: RealmConfig,
        connection: DatabaseConnection,
    ): GameDbResult<UpdateResponse> {
        val update =
            connection.prepareStatement(
                """
                    UPDATE realms
                    SET login_message = ?, login_broadcast = ?, player_xp_rate_in_hundreds = ?,
                    global_xp_rate_in_hundreds = ?
                    WHERE name = ?
                """
                    .trimIndent()
            )

        update.use {
            it.setNullableString(1, config.loginMessage)
            it.setNullableString(2, config.loginBroadcast)
            it.setInt(3, (config.baseXpRate * 100).toInt())
            it.setInt(4, (config.globalXpRate * 100).toInt())
            it.setString(5, realm.name)

            val updated = it.executeUpdate()
            return if (updated == 0) {
                GameDbResult.Ok(UpdateResponse.RealmNameNotFound)
            } else {
                GameDbResult.Ok(UpdateResponse.Success(config))
            }
        }
    }

    private fun applyChanges(uid: PlayerUid, result: GameDbResult<UpdateResponse>) {
        val player = uid.resolve(playerList)
        if (result.isErr()) {
            player?.mes("Failed to update realm config. Please try again later.")
            return
        }
        when (val response = result.value) {
            UpdateResponse.RealmNameNotFound -> {
                player?.mes("Severe error occurred! Realm could not be found: '${realm.name}'")
            }
            is UpdateResponse.Success -> {
                applyGlobalChanges(response.updated)
                player?.mes("Realm changes have been applied.")
            }
        }
    }

    private fun applyGlobalChanges(updated: RealmConfig) {
        val previous = realm.config

        realmUpdater.update(updated)

        if (previous.globalXpRate != updated.globalXpRate) {
            for (player in playerList) {
                player.globalXpRate = updated.globalXpRate
            }
        }

        val broadcast = updated.loginBroadcast
        if (previous.loginBroadcast != broadcast && broadcast != null) {
            for (player in playerList) {
                player.mes(broadcast, ChatType.Broadcast)
            }
        }
    }

    private object BaseXpRateSelection : ConfigSelection {
        override fun formatText(config: RealmConfig): String {
            return "Player XP Rates: ${config.baseXpRate}"
        }

        override suspend fun ProtectedAccess.onSelect(activeConfig: RealmConfig): RealmConfig {
            applicationWarning()
            val current = activeConfig.baseXpRate
            val newRate = countDialog("Enter base player xp rate: (current: $current)")
            return if (newRate.toDouble() != current) {
                activeConfig.copy(baseXpRate = newRate.toDouble())
            } else {
                activeConfig
            }
        }

        private suspend fun ProtectedAccess.applicationWarning() = startDialogue {
            mesbox(
                "This XP rate only affects players on their first login. " +
                    "It does not apply retroactively."
            )
            mesbox("Modifying the value here does not support fractions (e.g., 2.5x).")
        }
    }

    private object GlobalXpRateSelection : ConfigSelection {
        override fun formatText(config: RealmConfig): String {
            return "Global XP Rate: ${config.globalXpRate}"
        }

        override suspend fun ProtectedAccess.onSelect(activeConfig: RealmConfig): RealmConfig {
            applicationWarning()
            val current = activeConfig.globalXpRate
            val newRate = countDialog("Enter global xp rate: (current: $current)")
            return if (newRate.toDouble() != current) {
                activeConfig.copy(globalXpRate = newRate.toDouble())
            } else {
                activeConfig
            }
        }

        private suspend fun ProtectedAccess.applicationWarning() = startDialogue {
            mesbox(
                "This XP rate modifies all XP gains globally. " +
                    "Any change will take effect immediately for all players"
            )
            mesbox("Modifying the value here does not support fractions (e.g., 2.5x).")
        }
    }

    private object BroadcastSelection : ConfigSelection {
        private const val MAX_DISPLAY_LENGTH = 15
        private const val MAX_MESSAGE_LENGTH = 80 // Limited by `stringDialog`.
        private const val NULL_MESSAGE_CHAR = '.'

        override fun formatText(config: RealmConfig): String {
            val current = config.loginBroadcast?.takeBroadcast() ?: "-Not set-"
            return "Login Broadcast: $current"
        }

        override suspend fun ProtectedAccess.onSelect(activeConfig: RealmConfig): RealmConfig {
            applicationWarning()
            val message =
                stringDialog("Enter login broadcast message: (max $MAX_MESSAGE_LENGTH characters)")
            val clearBroadcast = message.length == 1 && message.first() == NULL_MESSAGE_CHAR
            return if (clearBroadcast) {
                activeConfig.copy(loginBroadcast = null)
            } else {
                activeConfig.copy(loginBroadcast = message.take(MAX_MESSAGE_LENGTH))
            }
        }

        private suspend fun ProtectedAccess.applicationWarning() = startDialogue {
            mesbox(
                "This message is broadcast to players upon login, and " +
                    "also sent immediately to all online players when confirmed."
            )
            mesbox("Enter a single '$NULL_MESSAGE_CHAR' to clear the broadcast message.")
        }

        private fun String.takeBroadcast(): String =
            if (length <= MAX_DISPLAY_LENGTH) this else take(MAX_DISPLAY_LENGTH - 3) + "..."
    }

    private sealed interface ConfigSelection {
        fun formatText(config: RealmConfig): String

        suspend fun ProtectedAccess.onSelect(activeConfig: RealmConfig): RealmConfig
    }

    private sealed class UpdateResponse {
        data class Success(val updated: RealmConfig) : UpdateResponse()

        data object RealmNameNotFound : UpdateResponse()
    }

    private companion object {
        private val selections =
            listOf(GlobalXpRateSelection, BaseXpRateSelection, BroadcastSelection)
    }
}
