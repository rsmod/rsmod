package org.runerealm.api.account.character.main

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import java.sql.Statement
import kotlin.math.roundToInt
import org.runerealm.api.account.character.CharacterMetadataList
import org.runerealm.api.db.DatabaseConnection
import org.runerealm.api.db.util.getIntOrNull
import org.runerealm.api.db.util.getLocalDateTime
import org.runerealm.api.db.util.setNullableInt
import org.runerealm.api.db.util.setNullableString
import org.runerealm.api.db.util.setSqliteTimestamp
import org.runerealm.api.parsers.jackson.readReifiedValue
import org.runerealm.api.parsers.json.Json
import org.runerealm.api.realm.Realm
import org.runerealm.game.entity.Player
import org.runerealm.game.type.varp.VarpLifetime
import org.runerealm.game.type.varp.VarpTypeList

public class CharacterAccountRepository
@Inject
constructor(
    @Json private val objectMapper: ObjectMapper,
    private val realm: Realm,
    private val applier: CharacterAccountApplier,
    private val varpTypes: VarpTypeList,
) {
    private val realmId: Int
        get() = realm.config.id

    public fun insertOrSelectAccountId(
        connection: DatabaseConnection,
        loginName: String,
        hashedPassword: String,
    ): Int? {
        val lowercaseName = loginName.lowercase()

        // Note: Not all database engines support `ON CONFLICT`. This syntax works with our current
        // database setup (sqlite) but may need to be adapted for others (e.g., mysql uses
        // `INSERT IGNORE`).
        val insert =
            connection.prepareStatement(
                """
                    INSERT INTO accounts (login_username, password_hash)
                    VALUES (?, ?)
                    ON CONFLICT(login_username) DO NOTHING
                """
                    .trimIndent()
            )
        insert.use {
            it.setString(1, lowercaseName)
            it.setString(2, hashedPassword)
            it.executeUpdate()
        }

        val select = connection.prepareStatement("SELECT id FROM accounts WHERE login_username = ?")
        val accountId =
            select.use {
                it.setString(1, lowercaseName)
                it.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        resultSet.getIntOrNull("id")
                    } else {
                        null
                    }
                }
            }
        return accountId
    }

    public fun insertAndSelectCharacterId(connection: DatabaseConnection, accountId: Int): Int? {
        val insert =
            connection.prepareStatement(
                """
                    INSERT INTO characters (account_id, realm_id)
                    VALUES (?, ?)
                """
                    .trimIndent(),
                Statement.RETURN_GENERATED_KEYS,
            )
        insert.use {
            it.setInt(1, accountId)
            it.setInt(2, realmId)

            val updateCount = it.executeUpdate()
            if (updateCount == 0) {
                return null
            }

            val characterId =
                insert.generatedKeys.use { keys ->
                    if (keys.next()) {
                        keys.getInt(1)
                    } else {
                        null
                    }
                }
            return characterId
        }
    }

    public fun selectAndCreateMetadataList(
        connection: DatabaseConnection,
        loginName: String,
    ): CharacterMetadataList? {
        val lowercaseName = loginName.lowercase()

        val select =
            connection.prepareStatement(
                """
                    SELECT
                        a.id AS account_id,
                        a.login_username,
                        a.display_name,
                        a.password_hash,
                        a.email,
                        a.members,
                        a.modlevel,
                        a.twofa_enabled,
                        a.twofa_secret,
                        a.twofa_last_verified,
                        a.known_device,
                        c.id AS character_id,
                        c.world_id,
                        c.x,
                        c.z,
                        c.level,
                        c.varps,
                        c.created_at AS character_created_at,
                        c.last_login,
                        c.last_logout,
                        c.muted_until,
                        c.banned_until,
                        c.run_energy,
                        c.xp_rate_in_hundreds,
                        c.appearance,
                        c.friends,
                        c.ignores,
                        c.friend_chat,
                        c.friend_chat_ranks
                    FROM accounts a
                    JOIN characters c ON c.account_id = a.id
                    WHERE c.realm_id = ?
                        AND a.login_username = ?
                """
                    .trimIndent()
            )

        select.use {
            it.setInt(1, realmId)
            it.setString(2, lowercaseName)
            it.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    val accountId = resultSet.getInt("account_id")
                    val characterId = resultSet.getInt("character_id")
                    val username = resultSet.getString("login_username").lowercase()
                    val displayName = resultSet.getString("display_name")
                    val hashedPassword = resultSet.getString("password_hash")
                    val email = resultSet.getString("email")
                    val members = resultSet.getBoolean("members")
                    val modLevel = resultSet.getString("modlevel")
                    val twofaEnabled = resultSet.getBoolean("twofa_enabled")
                    val twofaSecret = resultSet.getString("twofa_secret")
                    val twofaLastVerified = resultSet.getLocalDateTime("twofa_last_verified")
                    val device = resultSet.getIntOrNull("known_device")
                    val worldId = resultSet.getIntOrNull("world_id")
                    val coordX = resultSet.getInt("x")
                    val coordZ = resultSet.getInt("z")
                    val coordLevel = resultSet.getInt("level")
                    val varpsText = resultSet.getString("varps")
                    val createdAt = resultSet.getLocalDateTime("character_created_at")
                    val lastLogin = resultSet.getLocalDateTime("last_login")
                    val lastLogout = resultSet.getLocalDateTime("last_logout")
                    val mutedUntil = resultSet.getLocalDateTime("muted_until")
                    val bannedUntil = resultSet.getLocalDateTime("banned_until")
                    val runEnergy = resultSet.getInt("run_energy")
                    val xpRateInHundreds = resultSet.getInt("xp_rate_in_hundreds")
                    val appearanceText = resultSet.getString("appearance")
                    val varps = objectMapper.readReifiedValue<Map<Int, Int>>(varpsText)
                    val friends =
                        resultSet.getString("friends")?.let {
                            objectMapper.readReifiedValue<List<String>>(it)
                        } ?: emptyList()
                    val ignores =
                        resultSet.getString("ignores")?.let {
                            objectMapper.readReifiedValue<List<String>>(it)
                        } ?: emptyList()
                    val friendChat =
                        resultSet.getString("friend_chat")?.let {
                            objectMapper.readReifiedValue<CharacterFriendChatData>(it)
                        }
                    val friendChatRanks =
                        resultSet.getString("friend_chat_ranks")?.let {
                            objectMapper.readReifiedValue<Map<String, Int>>(it)
                        } ?: emptyMap()
                    val appearance =
                        appearanceText?.let {
                            objectMapper.readReifiedValue<CharacterAppearanceData>(it)
                        }
                    val characterData =
                        CharacterAccountData(
                            realm = realmId,
                            accountId = accountId,
                            characterId = characterId,
                            loginName = username,
                            displayName = displayName,
                            hashedPassword = hashedPassword,
                            email = email,
                            members = members,
                            modLevel = modLevel,
                            twofaEnabled = twofaEnabled,
                            twofaSecret = twofaSecret,
                            twofaLastVerified = twofaLastVerified,
                            knownDevice = device,
                            worldId = worldId,
                            coordX = coordX,
                            coordZ = coordZ,
                            coordLevel = coordLevel,
                            varps = varps,
                            createdAt = createdAt,
                            lastLogin = lastLogin,
                            lastLogout = lastLogout,
                            mutedUntil = mutedUntil,
                            bannedUntil = bannedUntil,
                            runEnergy = runEnergy,
                            xpRate = xpRateInHundreds / 100.0,
                            appearance = appearance,
                            friends = friends,
                            ignores = ignores,
                            friendChat = friendChat,
                            friendChatRanks = friendChatRanks,
                        )
                    val metadataList = CharacterMetadataList(characterData, mutableListOf())
                    metadataList.add(applier, characterData)
                    return metadataList
                }
            }
        }

        return null
    }

    public fun save(
        connection: DatabaseConnection,
        player: Player,
        accountId: Int,
        characterId: Int,
    ) {
        val updateCharacter =
            connection.prepareStatement(
                """
                    UPDATE characters
                    SET x = ?, z = ?, level = ?, varps = ?, last_login = ?, run_energy = ?,
                        xp_rate_in_hundreds = ?, appearance = ?, friends = ?, ignores = ?,
                        friend_chat = ?, friend_chat_ranks = ?, last_logout = CURRENT_TIMESTAMP
                    WHERE id = ?
                """
                    .trimIndent()
            )

        updateCharacter.use {
            val persistentVarps =
                player.vars.backing.filterKeys { id -> varpTypes[id]?.scope == VarpLifetime.Perm }
            val varpsJson = objectMapper.writeValueAsString(persistentVarps)
            it.setInt(1, player.x)
            it.setInt(2, player.z)
            it.setInt(3, player.level)
            it.setString(4, varpsJson)
            it.setSqliteTimestamp(5, player.lastLogin)
            it.setInt(6, player.runEnergy)
            it.setInt(7, (player.xpRate * 100).roundToInt())
            it.setString(8, objectMapper.writeValueAsString(player.appearanceData()))
            it.setString(9, objectMapper.writeValueAsString(player.friendList.toList()))
            it.setString(10, objectMapper.writeValueAsString(player.ignoreList.toList()))
            val friendChat = CharacterFriendChatData.from(player.friendChat)
            it.setString(11, objectMapper.writeValueAsString(friendChat))
            it.setString(12, objectMapper.writeValueAsString(player.friendChatRanks.toMap()))
            it.setInt(13, characterId)
            it.executeUpdate()
        }

        val updateAccount =
            connection.prepareStatement(
                """
                    UPDATE accounts
                    SET display_name = ?, known_device = ?, modlevel = ?
                    WHERE id = ?
                """
                    .trimIndent()
            )

        updateAccount.use {
            it.setNullableString(1, player.displayName.takeIf(String::isNotBlank))
            it.setNullableInt(2, player.lastKnownDevice)
            it.setNullableString(3, player.modLevel.internalName)
            it.setInt(4, accountId)
            it.executeUpdate()
        }
    }

    private fun Player.appearanceData(): CharacterAppearanceData =
        CharacterAppearanceData(
            bodyType = appearance.bodyType,
            pronoun = appearance.pronoun,
            identKit = appearance.identKitSnapshot().map(Short::toInt),
            colours = appearance.coloursSnapshot().map { it.toInt() and 0xFF },
        )
}
