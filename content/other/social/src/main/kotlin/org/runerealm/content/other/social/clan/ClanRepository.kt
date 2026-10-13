package org.runerealm.content.other.social.clan

import org.runerealm.api.db.DatabaseConnection

/** SQL for the `clans` and `clan_members` tables (migration V12). */
object ClanRepository {
    fun selectAll(connection: DatabaseConnection, realmId: Int): List<Clan> {
        val clans = linkedMapOf<Int, Clan>()
        val selectClans =
            connection.prepareStatement(
                "SELECT id, name, created_minute, talk_rank, kick_rank FROM clans WHERE realm_id = ?"
            )
        selectClans.use {
            it.setInt(1, realmId)
            it.executeQuery().use { rs ->
                while (rs.next()) {
                    val clan =
                        Clan(
                            id = rs.getInt("id"),
                            name = rs.getString("name"),
                            createdMinute = rs.getInt("created_minute"),
                            talkRank = rs.getInt("talk_rank"),
                            kickRank = rs.getInt("kick_rank"),
                        )
                    clans[clan.id] = clan
                }
            }
        }
        val selectMembers =
            connection.prepareStatement(
                """
                    SELECT m.clan_id, m.character_id, m.display_name, m.rank, m.join_rune_day
                    FROM clan_members m
                    JOIN clans c ON c.id = m.clan_id
                    WHERE c.realm_id = ?
                    ORDER BY m.rowid
                """
                    .trimIndent()
            )
        selectMembers.use {
            it.setInt(1, realmId)
            it.executeQuery().use { rs ->
                while (rs.next()) {
                    val clan = clans[rs.getInt("clan_id")] ?: continue
                    clan.members +=
                        ClanMember(
                            characterId = rs.getInt("character_id"),
                            displayName = rs.getString("display_name"),
                            rank = rs.getInt("rank"),
                            joinRuneDay = rs.getInt("join_rune_day"),
                        )
                }
            }
        }
        return clans.values.toList()
    }

    fun insertClan(connection: DatabaseConnection, realmId: Int, clan: Clan) {
        val insert =
            connection.prepareStatement(
                """
                    INSERT INTO clans (id, realm_id, name, created_minute, talk_rank, kick_rank)
                    VALUES (?, ?, ?, ?, ?, ?)
                """
                    .trimIndent()
            )
        insert.use {
            it.setInt(1, clan.id)
            it.setInt(2, realmId)
            it.setString(3, clan.name)
            it.setInt(4, clan.createdMinute)
            it.setInt(5, clan.talkRank)
            it.setInt(6, clan.kickRank)
            it.executeUpdate()
        }
        for (member in clan.members) {
            insertMember(connection, clan.id, member)
        }
    }

    fun insertMember(connection: DatabaseConnection, clanId: Int, member: ClanMember) {
        val insert =
            connection.prepareStatement(
                """
                    INSERT INTO clan_members (clan_id, character_id, display_name, rank, join_rune_day)
                    VALUES (?, ?, ?, ?, ?)
                """
                    .trimIndent()
            )
        insert.use {
            it.setInt(1, clanId)
            it.setInt(2, member.characterId)
            it.setString(3, member.displayName)
            it.setInt(4, member.rank)
            it.setInt(5, member.joinRuneDay)
            it.executeUpdate()
        }
    }

    fun updateMember(connection: DatabaseConnection, member: ClanMember) {
        val update =
            connection.prepareStatement(
                "UPDATE clan_members SET display_name = ?, rank = ? WHERE character_id = ?"
            )
        update.use {
            it.setString(1, member.displayName)
            it.setInt(2, member.rank)
            it.setInt(3, member.characterId)
            it.executeUpdate()
        }
    }

    fun deleteMember(connection: DatabaseConnection, characterId: Int) {
        val delete = connection.prepareStatement("DELETE FROM clan_members WHERE character_id = ?")
        delete.use {
            it.setInt(1, characterId)
            it.executeUpdate()
        }
    }

    fun deleteClan(connection: DatabaseConnection, clanId: Int) {
        val deleteMembers =
            connection.prepareStatement("DELETE FROM clan_members WHERE clan_id = ?")
        deleteMembers.use {
            it.setInt(1, clanId)
            it.executeUpdate()
        }
        val deleteClan = connection.prepareStatement("DELETE FROM clans WHERE id = ?")
        deleteClan.use {
            it.setInt(1, clanId)
            it.executeUpdate()
        }
    }
}
