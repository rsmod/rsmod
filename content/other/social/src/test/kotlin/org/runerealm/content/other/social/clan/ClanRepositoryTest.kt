package org.runerealm.content.other.social.clan

import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.runerealm.api.db.DatabaseConnection

/** Runs the real migrations on a scratch sqlite database and round-trips clans through it. */
class ClanRepositoryTest {
    @TempDir lateinit var dir: Path

    @Test
    fun `clans and members survive a save and load`() {
        val url = "jdbc:sqlite:${dir.resolve("test.db")}"
        Flyway.configure()
            .dataSource(url, "", "")
            .locations("classpath:db/migration")
            .load()
            .migrate()
        DriverManager.getConnection(url).use { raw ->
            val connection = DatabaseConnection(raw)
            insertCharacters(raw, 3)

            val clan = Clan(id = 1, name = "Lumbridge", createdMinute = 1234)
            clan.members += ClanMember(1, "Alice", ClanRanks.OWNER, 8000)
            clan.members += ClanMember(2, "Bob", ClanRanks.ADMINISTRATOR, 8000)
            ClanRepository.insertClan(connection, REALM, clan)
            ClanRepository.insertMember(
                connection,
                1,
                ClanMember(3, "Carol", ClanRanks.RECRUIT, 8001),
            )
            ClanRepository.updateMember(
                connection,
                ClanMember(2, "Bobby", ClanRanks.DEPUTY_OWNER, 0),
            )
            ClanRepository.deleteMember(connection, 1)

            val loaded = ClanRepository.selectAll(connection, REALM).single()
            assertEquals("Lumbridge", loaded.name)
            assertEquals(1234, loaded.createdMinute)
            assertEquals(listOf("Bobby", "Carol"), loaded.members.map { it.displayName })
            assertEquals(ClanRanks.DEPUTY_OWNER, loaded.members[0].rank)
            assertEquals(8001, loaded.members[1].joinRuneDay)

            ClanRepository.deleteClan(connection, 1)
            assertTrue(ClanRepository.selectAll(connection, REALM).isEmpty())
        }
        Files.deleteIfExists(dir.resolve("test.db"))
    }

    private fun insertCharacters(connection: java.sql.Connection, count: Int) {
        connection.createStatement().use { statement ->
            for (i in 1..count) {
                statement.executeUpdate(
                    "INSERT INTO accounts (id, login_username, password_hash) VALUES ($i, 'u$i', 'x')"
                )
                statement.executeUpdate(
                    "INSERT INTO characters (id, account_id, realm_id) VALUES ($i, $i, $REALM)"
                )
            }
        }
    }

    private companion object {
        const val REALM = 1
    }
}
