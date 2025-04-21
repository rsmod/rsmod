package org.rsmod.api.account.character.main

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import java.sql.Statement
import org.rsmod.api.account.character.CharacterMetadataList
import org.rsmod.api.db.Database
import org.rsmod.api.db.util.getLocalDateTime
import org.rsmod.api.db.util.prepareStatement
import org.rsmod.api.db.util.setNullableInt
import org.rsmod.api.db.util.setNullableString
import org.rsmod.api.db.util.setSqliteTimestamp
import org.rsmod.api.parsers.jackson.readReifiedValue
import org.rsmod.api.parsers.json.Json
import org.rsmod.api.server.config.WorldConfig
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varp.VarpLifetime
import org.rsmod.game.type.varp.VarpTypeList

public class CharacterAccountRepository
@Inject
constructor(
    @Json private val objectMapper: ObjectMapper,
    private val worldConfig: WorldConfig,
    private val applier: CharacterAccountApplier,
    private val varpTypes: VarpTypeList,
) {
    private val realm: Int
        get() = worldConfig.realm.id

    public suspend fun insertOrSelectAccountId(
        database: Database,
        loginName: String,
        hashedPassword: String,
    ): Int? {
        val lowercaseName = loginName.lowercase()

        // Note: Not all database engines support `ON CONFLICT`. This syntax works with our current
        // database setup (sqlite) but may need to be adapted for others (e.g., mysql uses
        // `INSERT IGNORE`).
        val insert =
            database.prepareStatement(
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

        val select = database.prepareStatement("SELECT id FROM accounts WHERE login_username = ?")
        val accountId =
            select.use {
                it.setString(1, lowercaseName)
                it.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        resultSet.getInt("id").takeUnless { resultSet.wasNull() }
                    } else {
                        null
                    }
                }
            }
        return accountId
    }

    public suspend fun insertAndSelectCharacterId(database: Database, accountId: Int): Int? {
        val insert =
            database.prepareStatement(
                """
                    INSERT INTO characters (account_id, realm_id)
                    VALUES (?, ?)
                """
                    .trimIndent(),
                Statement.RETURN_GENERATED_KEYS,
            )
        insert.use {
            it.setInt(1, accountId)
            it.setInt(2, realm)

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

    public suspend fun selectAndCreateMetadataList(
        database: Database,
        loginName: String,
    ): CharacterMetadataList? {
        val lowercaseName = loginName.lowercase()

        val select =
            database.prepareStatement(
                """
                    SELECT
                        a.id AS account_id,
                        a.login_username,
                        a.display_name,
                        a.password_hash,
                        a.email,
                        a.members,
                        a.mod_group,
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
                        c.banned_until
                    FROM accounts a
                    JOIN characters c ON c.account_id = a.id
                    WHERE c.realm_id = ?
                        AND a.login_username = ?
                """
                    .trimIndent()
            )

        select.use {
            it.setInt(1, realm)
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
                    val modGroup = resultSet.getInt("mod_group").takeUnless { resultSet.wasNull() }
                    val twofaEnabled = resultSet.getBoolean("twofa_enabled")
                    val twofaSecret = resultSet.getString("twofa_secret")
                    val twofaLastVerified = resultSet.getLocalDateTime("twofa_last_verified")
                    val device = resultSet.getInt("known_device").takeUnless { resultSet.wasNull() }
                    val worldId = resultSet.getInt("world_id").takeUnless { resultSet.wasNull() }
                    val coordX = resultSet.getInt("x")
                    val coordZ = resultSet.getInt("z")
                    val coordLevel = resultSet.getInt("level")
                    val varpsText = resultSet.getString("varps")
                    val createdAt = resultSet.getLocalDateTime("character_created_at")
                    val lastLogin = resultSet.getLocalDateTime("last_login")
                    val lastLogout = resultSet.getLocalDateTime("last_logout")
                    val mutedUntil = resultSet.getLocalDateTime("muted_until")
                    val bannedUntil = resultSet.getLocalDateTime("banned_until")
                    val varps = objectMapper.readReifiedValue<Map<Int, Int>>(varpsText)
                    val characterData =
                        CharacterAccountData(
                            realm = realm,
                            accountId = accountId,
                            characterId = characterId,
                            loginName = username,
                            displayName = displayName,
                            hashedPassword = hashedPassword,
                            email = email,
                            members = members,
                            modGroup = modGroup,
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
                        )
                    val metadataList = CharacterMetadataList(characterData, mutableListOf())
                    metadataList.add(applier, characterData)
                    return metadataList
                }
            }
        }

        return null
    }

    public suspend fun save(database: Database, player: Player, accountId: Int, characterId: Int) {
        val updateCharacter =
            database.prepareStatement(
                """
                    UPDATE characters
                    SET x = ?, z = ?, level = ?, varps = ?, last_login = ?,
                        last_logout = CURRENT_TIMESTAMP
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
            it.setInt(6, characterId)
            it.executeUpdate()
        }

        val updateAccount =
            database.prepareStatement(
                """
                    UPDATE accounts
                    SET display_name = ?, known_device = ?, mod_group = ?
                    WHERE id = ?
                """
                    .trimIndent()
            )

        updateAccount.use {
            it.setNullableString(1, player.displayName.takeIf(String::isNotBlank))
            it.setNullableInt(2, player.lastKnownDevice)
            it.setNullableInt(3, player.modGroup?.id)
            it.setInt(4, accountId)
            it.executeUpdate()
        }
    }
}
