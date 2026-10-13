package org.runerealm.content.skills.magic.teleports

import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.api.type.refs.spot.SpotanimReferences
import org.runerealm.map.CoordGrid

/** Animations and timing shared by item teleports (wiki: Teleportation). */
internal object ItemTeleports {
    /** Jewellery may be used up to level 30 Wilderness, tablets up to level 20. */
    const val JEWELLERY_WILDERNESS_LIMIT = 30
    const val TABLET_WILDERNESS_LIMIT = 20

    fun canTeleport(access: ProtectedAccess, wildernessLimit: Int): Boolean {
        val level = WildernessArea.level(access.player.coords)
        if (WildernessArea.contains(access.player.coords) && level > wildernessLimit) {
            access.mes("A mysterious force blocks your teleport spell!")
            access.mes("You can't use this teleport after level $wildernessLimit wilderness.")
            return false
        }
        return true
    }

    /** The standard spellbook-style teleport used by jewellery: 3 ticks, then arrival. */
    suspend fun jewelleryTeleport(access: ProtectedAccess, dest: CoordGrid) {
        access.anim(teleport_seqs.cast)
        access.spotanim(teleport_spots.casting, height = 92)
        access.delay(3)
        access.telejump(dest)
        access.resetAnim()
    }

    /** Breaking a tablet: the break animation, then absorbing its magic, then arrival. */
    suspend fun tabletTeleport(access: ProtectedAccess, dest: CoordGrid) {
        access.anim(teleport_seqs.tablet_break)
        access.delay(1)
        access.anim(teleport_seqs.tablet_absorb)
        access.spotanim(teleport_spots.tablet_absorb)
        access.delay(2)
        access.telejump(dest)
        access.resetAnim()
    }
}

internal typealias teleport_seqs = ItemTeleportSeqs

internal typealias teleport_spots = ItemTeleportSpots

object ItemTeleportSeqs : SeqReferences() {
    val cast = find("human_castteleport")
    val tablet_break = find("poh_smash_magic_tablet")
    val tablet_absorb = find("poh_absorb_tablet_teleport")
}

object ItemTeleportSpots : SpotanimReferences() {
    val casting = find("teleport_casting")
    val tablet_absorb = find("poh_absorb_tablet_magic")
}
