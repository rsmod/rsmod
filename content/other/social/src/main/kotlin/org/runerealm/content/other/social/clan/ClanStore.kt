package org.runerealm.content.other.social.clan

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.db.DatabaseConnection
import org.runerealm.api.db.gateway.GameDbManager
import org.runerealm.api.db.gateway.model.GameDbResult
import org.runerealm.api.db.gateway.model.isErr
import org.runerealm.api.realm.Realm

/**
 * Saves clan changes in the background. The in-memory [ClanRegistry] is the source of truth while
 * the server runs, so writes do not wait for the database; a failed write is logged.
 *
 * Every write captures copies of the values it saves, since the database runs on another thread.
 */
class ClanStore @Inject constructor(private val db: GameDbManager, private val realm: Realm) {
    private val logger = InlineLogger()

    private val realmId: Int
        get() = realm.config.id

    /**
     * Reads every clan of this realm. The realm id is resolved on the database thread, as the realm
     * config is not yet available while plugin scripts start up. If the read fails, [onLoaded] is
     * never called and no clans can be formed (new clan ids could clash with the saved ones).
     */
    fun loadAll(onLoaded: (List<Clan>) -> Unit) {
        db.request(
            request = { GameDbResult.Ok(ClanRepository.selectAll(it, realmId)) },
            response = { result ->
                if (result.isErr()) {
                    logger.error { "Could not load clans: $result" }
                } else {
                    onLoaded(result.value)
                }
            },
        )
    }

    fun insertClan(clan: Clan) {
        val copy = clan.snapshot()
        val realmId = realmId
        write("insert clan ${clan.name}") { ClanRepository.insertClan(it, realmId, copy) }
    }

    fun insertMember(clan: Clan, member: ClanMember) {
        val copy = member.snapshot()
        val clanId = clan.id
        write("add ${member.displayName} to clan $clanId") {
            ClanRepository.insertMember(it, clanId, copy)
        }
    }

    fun updateMember(member: ClanMember) {
        val copy = member.snapshot()
        write("update clan member ${member.displayName}") { ClanRepository.updateMember(it, copy) }
    }

    fun deleteMember(member: ClanMember) {
        val characterId = member.characterId
        write("remove clan member ${member.displayName}") {
            ClanRepository.deleteMember(it, characterId)
        }
    }

    fun deleteClan(clan: Clan) {
        val clanId = clan.id
        write("delete clan ${clan.name}") { ClanRepository.deleteClan(it, clanId) }
    }

    private fun write(description: String, block: (DatabaseConnection) -> Unit) {
        db.request(
            request = {
                block(it)
                GameDbResult.Ok(Unit)
            },
            response = { result ->
                if (result.isErr()) {
                    logger.error { "Could not $description: $result" }
                }
            },
        )
    }

    private fun Clan.snapshot(): Clan {
        val copy = Clan(id, name, createdMinute, talkRank, kickRank)
        copy.members += members.map { it.snapshot() }
        return copy
    }

    private fun ClanMember.snapshot(): ClanMember =
        ClanMember(characterId, displayName, rank, joinRuneDay)
}
