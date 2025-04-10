package org.rsmod.api.account.character.main

import java.time.LocalDateTime
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid

public class CharacterAccountApplier : CharacterDataStage.Applier<CharacterAccountData> {
    override fun apply(player: Player, data: CharacterAccountData) {
        val accountHash = (data.accountId.toLong() shl 32) or data.realm.toLong()
        val characterId = data.characterId
        player.accountHash = accountHash
        player.characterId = characterId
        player.userId = characterId.toLong()

        val uuid = data.characterId.toLong()
        player.uuid = uuid
        player.observerUUID = uuid

        player.username = data.loginName
        player.displayName = data.displayName ?: ""
        player.coords = CoordGrid(data.coordX, data.coordZ, data.coordLevel)
        player.lastLogin = LocalDateTime.now()
        player.vars.backing.putAll(data.varps)
    }
}
