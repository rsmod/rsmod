package org.rsmod.api.account.character.main

import java.time.LocalDateTime
import org.rsmod.api.account.character.CharacterDataStage

public data class CharacterAccountData(
    val realm: Int,
    val accountId: Int,
    val characterId: Int,
    val loginName: String,
    val displayName: String?,
    val hashedPassword: String,
    val email: String?,
    val members: Boolean,
    val modGroup: Int?,
    val twofaEnabled: Boolean,
    val twofaSecret: String?,
    val twofaLastVerified: LocalDateTime?,
    val worldId: Int?,
    val coordX: Int,
    val coordZ: Int,
    val coordLevel: Int,
    val varps: Map<Int, Int>,
    val createdAt: LocalDateTime?,
    val lastLogin: LocalDateTime?,
    val lastLogout: LocalDateTime?,
    val mutedUntil: LocalDateTime?,
    val bannedUntil: LocalDateTime?,
) : CharacterDataStage.Segment
