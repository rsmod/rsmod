package org.rsmod.content.other.canoe.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.other.canoe.configs.canoe_locs
import org.rsmod.content.other.canoe.configs.canoe_seqs
import org.rsmod.content.other.canoe.configs.canoe_synths
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CanoePushing @Inject constructor(private val worldRepo: WorldRepository) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(canoe_locs.ready_log) { pushCanoe(it.loc, Canoe.Log) }
        onOpLoc1(canoe_locs.ready_dugout) { pushCanoe(it.loc, Canoe.Dugout) }
        onOpLoc1(canoe_locs.ready_stable_dugout) { pushCanoe(it.loc, Canoe.StableDugout) }
        onOpLoc1(canoe_locs.ready_waka) { pushCanoe(it.loc, Canoe.Waka) }
    }

    private suspend fun ProtectedAccess.pushCanoe(loc: BoundLocInfo, canoe: Canoe) {
        check(canoeType == canoe) { "Unexpected canoe type: expected=$canoeType, actual=$canoe" }
        val station = resolveStation()
        val dest = station.pushStartCoords()

        if (coords != dest) {
            walk(coords)
            delay(1)
            playerWalk(dest)
            faceSquare(dest)
            delay(1)
        }

        this[station, canoe] = CanoeState.Pushing
        anim(canoe_seqs.canoeing_pushing_into_water)
        soundSynth(canoe_synths.canoe_pushed)
        worldRepo.locAnim(loc, canoe_seqs.canoeing_station_animations)
        delay(1)
        faceSquare(loc.adjustedCentre)
        delay(1)
        this[station, canoe] = CanoeState.Floating
    }

    private fun Station.pushStartCoords(): CoordGrid =
        when (this) {
            Station.Lumbridge -> CoordGrid(0, 50, 50, 43, 37)
            Station.ChampionsGuild -> CoordGrid(0, 50, 52, 2, 15)
            Station.BarbarianVillage -> CoordGrid(0, 48, 53, 40, 19)
            Station.Edgeville -> CoordGrid(0, 48, 54, 60, 54)
            Station.FeroxEnclave -> CoordGrid(0, 49, 56, 18, 46)
        }
}
