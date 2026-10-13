package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.type.builders.varp.VarpBuilder
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.game.entity.Player

/**
 * The Ranged and Magic combat tutors share one claim: every 30 minutes a player may claim either
 * training arrows or runes, but not both ([Transcript:Ranged combat
 * tutor](https://oldschool.runescape.wiki/w/Transcript:Ranged_combat_tutor)).
 */
internal object TutorClaims {
    private const val COOLDOWN_MINUTES = 30

    fun onCooldown(player: Player, nowMinutes: Int = nowMinutes()): Boolean {
        val last = player.lastClaim
        return last != 0 && nowMinutes - last < COOLDOWN_MINUTES
    }

    fun markClaimed(player: Player, nowMinutes: Int = nowMinutes()) {
        player.lastClaim = nowMinutes
    }

    /**
     * The shared "cheeky people" reply; [item] is "arrows" or "runes", [source] how to get more.
     */
    suspend fun Dialogue.cooldownReply(otherTutor: String, item: String, source: String) {
        chatNpc(
            neutral,
            "I work with the $otherTutor to give out consumable items that you may need for " +
                "combat such as arrows and runes. However we have had some cheeky people try to " +
                "take both!",
        )
        chatNpc(
            neutral,
            "So, every half an hour, you may come back and claim either arrows OR runes, but " +
                "not both. Come back in a while for $item, or simply $source.",
        )
    }

    private fun nowMinutes(): Int = (System.currentTimeMillis() / 60_000).toInt()
}

internal object TutorClaimVarps : VarpReferences() {
    /** Wall-clock minute (since the epoch) of the last Ranged/Magic tutor claim. */
    val last_claim = find("aide_tutor_last_claim")
}

internal object TutorClaimVarpBuilder : VarpBuilder() {
    init {
        build("aide_tutor_last_claim")
    }
}

private var Player.lastClaim by intVarp(TutorClaimVarps.last_claim)
