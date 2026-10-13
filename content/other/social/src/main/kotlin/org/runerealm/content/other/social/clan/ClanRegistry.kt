package org.runerealm.content.other.social.clan

import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import org.runerealm.game.entity.Player

/** A clan and its ranked ("affined") members, following the OSRS Wiki's Clan page. */
class Clan(
    val id: Int,
    var name: String,
    val createdMinute: Int,
    var talkRank: Int = ClanRanks.RECRUIT,
    var kickRank: Int = ClanRanks.ADMINISTRATOR,
) {
    val members = mutableListOf<ClanMember>()

    /** Online members currently in the clan's chat channel. */
    val channel = mutableListOf<Player>()

    /** Bumped on every change, as the client expects of its clan update counters. */
    var updateNum: Int = 0

    fun member(characterId: Int): ClanMember? =
        members.firstOrNull { it.characterId == characterId }

    val owner: ClanMember?
        get() = members.firstOrNull { it.rank == ClanRanks.OWNER }
}

class ClanMember(
    val characterId: Int,
    var displayName: String,
    var rank: Int,
    val joinRuneDay: Int,
)

/** A clan being formed in the Clan Hub: the founder plus the players who accepted an invite. */
class ClanFormation(val founder: Player) {
    var name: String? = null
    val members = mutableListOf(founder)
}

/** OSRS clan ranks as the client understands them. */
object ClanRanks {
    const val GUEST = -1
    const val RECRUIT = 0
    const val ADMINISTRATOR = 100
    const val DEPUTY_OWNER = 125
    const val OWNER = 126
}

/** Every clan, kept in memory and saved through [ClanRepository]. */
@Singleton
class ClanRegistry {
    private val clans = mutableMapOf<Int, Clan>()
    private val formations = mutableListOf<ClanFormation>()

    /** Whether the saved clans have been read from the database; no clan is formed before then. */
    var loaded: Boolean = false
        private set

    val all: Collection<Clan>
        get() = clans.values

    fun load(saved: Collection<Clan>) {
        for (clan in saved) {
            clans[clan.id] = clan
        }
        loaded = true
    }

    /** Forgets every clan and formation (used by tests that share one game instance). */
    fun reset() {
        clans.clear()
        formations.clear()
        loaded = true
    }

    fun add(clan: Clan) {
        clans[clan.id] = clan
    }

    fun remove(clan: Clan) {
        clans.remove(clan.id)
    }

    fun nextId(): Int = (clans.keys.maxOrNull() ?: 0) + 1

    fun clanOf(player: Player): Clan? =
        clans.values.firstOrNull { it.member(player.characterId) != null }

    fun isNameTaken(name: String): Boolean =
        clans.values.any { it.name.equals(name, ignoreCase = true) } ||
            formations.any { it.name.equals(name, ignoreCase = true) }

    fun formationOf(player: Player): ClanFormation? =
        formations.firstOrNull { player in it.members }

    fun startFormation(founder: Player): ClanFormation =
        ClanFormation(founder).also { formations += it }

    fun endFormation(formation: ClanFormation) {
        formations -= formation
    }

    companion object {
        private val RUNE_DAY_ZERO: LocalDate = LocalDate.of(2002, 2, 27)

        fun runeDay(): Int = ChronoUnit.DAYS.between(RUNE_DAY_ZERO, LocalDate.now()).toInt()

        fun epochMinute(): Int = (System.currentTimeMillis() / 60_000).toInt()
    }
}
