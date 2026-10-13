package org.runerealm.content.generic.locs.staircase

import jakarta.inject.Inject
import org.runerealm.api.config.refs.content
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLoc2
import org.runerealm.api.script.onOpLoc3
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.loc.LocAngle
import org.runerealm.game.loc.LocInfo
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.map.CoordGrid
import org.runerealm.map.util.Translation
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SpiralStaircaseScript
@Inject
constructor(private val locRepo: LocRepository, private val locTypes: LocTypeList) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.spiralstaircase_down) { climbDown(it.loc) }
        onOpLoc1(content.spiralstaircase_up) { climbUp(it.loc) }
        onOpLoc1(content.spiralstaircase_option) { climOption(it.loc) }
        onOpLoc2(content.spiralstaircase_option) {
            arriveDelay()
            climbUp(it.loc)
        }
        onOpLoc3(content.spiralstaircase_option) {
            arriveDelay()
            climbDown(it.loc)
        }

        // "Top-floor"/"Bottom-floor" only exist on the ground and top floor staircases of taller
        // stacks (e.g. Lumbridge Castle's `spiralstairsbottom_3`/`spiralstairstop_3`).
        onOpLoc2(content.spiralstaircase_up) { climbToTop(it.loc) }
        onOpLoc2(content.spiralstaircase_down) { climbToBottom(it.loc) }
    }

    private fun ProtectedAccess.climbDown(loc: BoundLocInfo) =
        climb(loc, loc.climbDownTranslation())

    private fun BoundLocInfo.climbDownTranslation(): Translation =
        when (angle) {
            LocAngle.West -> Translation(x = adjustedWidth - 1, z = -1, level = -1)
            LocAngle.North -> Translation(x = -adjustedWidth, z = 0, level = -1)
            LocAngle.East -> Translation(x = 0, z = adjustedLength, level = -1)
            LocAngle.South -> Translation(x = adjustedWidth, z = adjustedLength - 1, level = -1)
        }

    private fun ProtectedAccess.climbUp(loc: BoundLocInfo) = climb(loc, loc.climbUpTranslation())

    private fun BoundLocInfo.climbUpTranslation(): Translation =
        when (angle) {
            LocAngle.West -> Translation(x = adjustedWidth, z = 0, level = 1)
            LocAngle.North -> Translation(x = 0, z = -(adjustedLength - 1), level = 1)
            LocAngle.East ->
                Translation(x = -(adjustedWidth - 1), z = adjustedLength - 1, level = 1)
            LocAngle.South -> Translation(x = adjustedWidth - 1, z = adjustedLength, level = 1)
        }

    private fun ProtectedAccess.climb(loc: BoundLocInfo, translation: Translation) {
        val dest = loc.coords.translate(translation)
        telejump(dest)
    }

    private suspend fun ProtectedAccess.climOption(loc: BoundLocInfo) {
        startDialogue {
            val translation =
                choice2(
                    "Climb up the stairs.",
                    loc.climbUpTranslation(),
                    "Climb down the stairs.",
                    loc.climbDownTranslation(),
                    title = "Climb up or down the stairs?",
                )
            val dest = loc.coords.translate(translation)
            telejump(dest)
        }
    }

    /** Climbs to the highest floor of the staircase stack that [loc] belongs to. */
    private fun ProtectedAccess.climbToTop(loc: BoundLocInfo) {
        val floors = loc.floorsInDirection(step = 1)
        climb(loc, loc.climbUpTranslation().copy(level = floors))
    }

    /** Climbs to the lowest floor of the staircase stack that [loc] belongs to. */
    private fun ProtectedAccess.climbToBottom(loc: BoundLocInfo) {
        val floors = loc.floorsInDirection(step = -1)
        climb(loc, loc.climbDownTranslation().copy(level = -floors))
    }

    /**
     * Counts how many floors the staircase stack continues in [step]'s direction from [this] loc.
     * Staircases of a stack overlap on every floor, but their origin tile can shift by a tile
     * between floors (e.g. Lumbridge Castle), so nearby tiles are searched too.
     */
    private fun BoundLocInfo.floorsInDirection(step: Int): Int {
        var floors = 1
        var level = coords.level + step
        while (level in 0..3) {
            val next = locAt(coords.copy(level = level)) ?: break
            // The end of the stack is a staircase that only leads back the way we came.
            if (locTypes[next.id]?.contentGroup != content.spiralstaircase_option.id) {
                break
            }
            floors++
            level += step
        }
        return floors
    }

    private fun locAt(coords: CoordGrid): LocInfo? =
        STACK_OFFSETS.firstNotNullOfOrNull { (dx, dz) ->
            locRepo.findAll(coords.translate(dx, dz)).firstOrNull {
                locTypes[it.id]?.contentGroup in staircaseGroups
            }
        }

    private companion object {
        /** Tile offsets searched for the next floor's staircase, nearest first. */
        val STACK_OFFSETS: List<Pair<Int, Int>> =
            (-1..1)
                .flatMap { dx -> (-1..1).map { dz -> dx to dz } }
                .sortedBy { (dx, dz) -> dx * dx + dz * dz }
    }

    private val staircaseGroups =
        setOf(
            content.spiralstaircase_up.id,
            content.spiralstaircase_down.id,
            content.spiralstaircase_option.id,
        )
}
