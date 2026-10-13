package org.runerealm.api.account.character.main

import jakarta.inject.Inject
import java.time.LocalDateTime
import org.runerealm.api.account.character.CharacterDataStage
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.FriendChatSettings
import org.runerealm.game.type.mod.ModLevelTypeList
import org.runerealm.game.type.varp.VarpLifetime
import org.runerealm.game.type.varp.VarpTypeList
import org.runerealm.map.CoordGrid

public class CharacterAccountApplier
@Inject
constructor(private val modLevelTypes: ModLevelTypeList, private val varpTypes: VarpTypeList) :
    CharacterDataStage.Applier<CharacterAccountData> {
    override fun apply(player: Player, data: CharacterAccountData) {
        player.accountId = data.accountId
        player.characterId = data.characterId

        val accountHash = (data.accountId.toLong() shl 32) or data.realm.toLong()
        val userHash = data.loginName.hashCode().toLong()
        player.userId = data.characterId.toLong()
        player.accountHash = accountHash
        player.userHash = userHash

        val uuid = data.characterId.toLong()
        player.uuid = uuid
        player.observerUUID = uuid

        val device = data.knownDevice
        player.lastKnownDevice = device
        player.username = data.loginName
        player.displayName = data.displayName ?: ""
        player.coords = CoordGrid(data.coordX, data.coordZ, data.coordLevel)
        player.runEnergy = data.runEnergy
        player.xpRate = data.xpRate
        player.lastLogin = LocalDateTime.now()
        // Only restore varps that are still persistent. Saves are keyed by varp id, so a
        // server-only
        // varp that was renumbered (or made temporary) would otherwise load a stale value into
        // whatever varp now owns that id.
        for ((id, value) in data.varps) {
            if (varpTypes[id]?.scope == VarpLifetime.Perm) {
                player.vars.backing[id] = value
            }
        }
        player.assignModLevel(data.modLevel)
        data.appearance?.let { player.assignAppearance(it) }
        player.friendList.clear()
        player.friendList.addAll(data.friends)
        player.ignoreList.clear()
        player.ignoreList.addAll(data.ignores)
        player.friendChatRanks.clear()
        player.friendChatRanks.putAll(data.friendChatRanks)
        player.friendChat = data.friendChat?.toSettings() ?: FriendChatSettings.DEFAULT
    }

    private fun Player.assignAppearance(data: CharacterAppearanceData) {
        appearance.bodyType = data.bodyType
        appearance.pronoun = data.pronoun
        for ((index, kit) in data.identKit.withIndex()) {
            appearance.setIdentKit(index, kit)
        }
        for ((index, colour) in data.colours.withIndex()) {
            appearance.setColour(index, colour)
        }
    }

    private fun Player.assignModLevel(modLevelName: String?) {
        val match =
            if (modLevelName != null) {
                modLevelTypes.values.firstOrNull { it.internalName == modLevelName }
            } else {
                null
            }
        modLevel = match ?: modLevelTypes.default()
    }
}
