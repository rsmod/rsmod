package org.rsmod.content.minigame.gamble

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpHeld1
import org.rsmod.content.minigame.gamble.config.flowers
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.routefinder.loc.LocLayerConstants

class MithrilSeeds
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val locRepo: LocRepository,
) : PluginScript() {

    companion object {
        private val FLOWER_TYPES = listOf(
            flowers.flower_waterfall_quest,
            flowers.flower_waterfall_quest_red,
            flowers.flower_waterfall_quest_blue,
            flowers.flower_waterfall_quest_yellow,
            flowers.flower_waterfall_quest_purple,
            flowers.flower_waterfall_quest_orange,
            flowers.flower_waterfall_quest_mixed,
            flowers.flower_waterfall_quest_white,
            flowers.flower_waterfall_quest_black,
        )

        // Pre-compute directions to avoid recreating the list each time
        private val ADJACENT_DIRECTIONS = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        // Constants for flower spawning
        private const val FLOWER_DURATION = 750
        private const val FLOWER_LAYER = 2

        //Delay time in ticks (game-cycles)
        private const val DELAY_TIME = 1
    }

    override fun ScriptContext.startup() {
        onOpHeld1(objs.mithril_seed) {
            plant(player.coords, FLOWER_TYPES)
        }
    }

    private suspend fun ProtectedAccess.plant(coords: CoordGrid, flowerTypes: List<LocType>) {

        if (hasBlockingLocationAtPosition(coords)) {
            mes("You can't plant your flowers here.")
            return
        }

        val validLocation = findValidPlantingLocation()
        if (validLocation == null) {
            mes("There are no free spaces to plant the seed.")
            return
        }

        val selectedFlowerType = locTypes[flowerTypes.random()]
        spawnFlower(selectedFlowerType, coords)

        invDel(player.inv, objs.mithril_seed, count = 1)
        mes("You open the small mithril case.")
        mes("You drop a seed by your feet.")

        // Perform planting animation sequence
        resetAnim()
        walk(validLocation)
        delay(DELAY_TIME)
        faceSquare(coords)


        // Handle player choice
//        handleFlowerChoice(selectedFlowerType, flower)
    }

    private fun ProtectedAccess.findValidPlantingLocation(): CoordGrid? {
        return ADJACENT_DIRECTIONS
            .asSequence() // Use sequence for lazy evaluation
            .map { (dx, dz) -> player.coords.translate(dx, dz) }
            .find { lineOfWalk(player.coords, it) }
    }

    private fun hasBlockingLocationAtPosition(coords: CoordGrid): Boolean {
        val locsAtPosition = locRepo.findAll(ZoneKey.from(coords))
            .filter { it.coords == coords && it.layer == FLOWER_LAYER }

        return locsAtPosition.any { loc ->
            val type = locTypes[loc.id]
            type?.active == -1
        }
    }

    private suspend fun ProtectedAccess.handleFlowerChoice(type: LocType, flower: LocInfo) {
        val choice = choice2(
            "Pick the flowers.", 1,
            "Leave the flowers.", 2
        )

        if (choice == 1) {
            takeFlowers(type, flower)
        }

    }

    private fun spawnFlower(type: LocType, coords: CoordGrid): LocInfo {
        val angle = LocAngle.West.id
        val shape = LocShape.CentrepieceStraight.id
        val layer = LocLayerConstants.of(shape)
        val loc = LocInfo(layer, coords, LocEntity(type.id, shape, angle))
        locRepo.add(loc, FLOWER_DURATION)
        return loc
    }

    private fun takeFlowers(type: LocType, loc: LocInfo) {
        locRepo.del(loc, 0)
    }
}

