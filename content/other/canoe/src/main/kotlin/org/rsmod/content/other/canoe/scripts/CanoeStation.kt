package org.rsmod.content.other.canoe.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.woodcuttingLvl
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc3
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.content.other.canoe.configs.canoe_enums
import org.rsmod.content.other.canoe.configs.canoe_locs
import org.rsmod.content.other.canoe.configs.canoe_seqs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.map.translate
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CanoeStation
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val enumTypes: EnumTypeList,
    private val invisibleLvls: InvisibleLevels,
    private val worldRepo: WorldRepository,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(canoe_locs.station_lumbridge) { pathTo(it.loc, Path.Lumbridge) }
        onOpLoc3(canoe_locs.station_lumbridge) { cut(it.loc, Path.Lumbridge) }

        onOpLoc1(canoe_locs.station_champs_guild) { pathTo(it.loc, Path.ChampionsGuild) }
        onOpLoc3(canoe_locs.station_champs_guild) { cut(it.loc, Path.ChampionsGuild) }

        onOpLoc1(canoe_locs.station_barb_village) { pathTo(it.loc, Path.BarbarianVillage) }
        onOpLoc3(canoe_locs.station_barb_village) { cut(it.loc, Path.BarbarianVillage) }

        onOpLoc1(canoe_locs.station_edgeville) { pathTo(it.loc, Path.Edgeville) }
        onOpLoc3(canoe_locs.station_edgeville) { cut(it.loc, Path.Edgeville) }

        onOpLoc1(canoe_locs.station_ferox_enclave) { pathTo(it.loc, Path.FeroxEnclave) }
        onOpLoc3(canoe_locs.station_ferox_enclave) { cut(it.loc, Path.FeroxEnclave) }
    }

    private suspend fun ProtectedAccess.pathTo(loc: BoundLocInfo, path: Path) {
        val (station, dest, face) = path

        assertState(station, StationState.StationFullyGrown)

        // Reset any previously set-up canoe station in other locations.
        val previousStation = canoeStation
        if (previousStation != null && previousStation != station) {
            clearStation(previousStation)
        }

        // Reset the "canoe shape" confirmation flag. (only when necessary)
        if (confirmedCanoeType) {
            confirmedCanoeType = false
        }

        canoeStation = station
        if (coords != dest) {
            walk(coords)
            delay(1)
            playerWalk(dest)
            delay(1)
        }
        faceSquare(face)
        delay(1)
        attempt(loc, path)
    }

    private suspend fun ProtectedAccess.attempt(loc: BoundLocInfo, path: Path) {
        if (player.woodcuttingLvl < 12) {
            mes("You must have at least level 12 woodcutting to start making canoes.")
            return
        }

        val axe = findAxe(player, objTypes)
        if (axe == null) {
            mes(
                "You need an axe to chop down this tree.<br>" +
                    "You do not have an axe which you have the Woodcutting level to use."
            )
            return
        }

        if (actionDelay < mapClock) {
            actionDelay = mapClock + 2
            refaceDelay = mapClock + 4
            skillAnimDelay = mapClock + 1
            opLoc1(loc)
            return
        }

        cut(loc, path)
    }

    private suspend fun ProtectedAccess.cut(loc: BoundLocInfo, path: Path) {
        val axe = findAxe(player, objTypes)
        if (axe == null) {
            mes(
                "You need an axe to chop down this tree.<br>" +
                    "You do not have an axe which you have the Woodcutting level to use."
            )
            return
        }

        if (player.woodcuttingLvl < 12) {
            mes("You must have at least level 12 woodcutting to start making canoes.")
            return
        }

        if (skillAnimDelay < mapClock) {
            skillAnimDelay = mapClock + 3
        } else if (skillAnimDelay == mapClock) {
            anim(objTypes[axe].axeWoodcuttingAnim)
        }

        if (refaceDelay < mapClock) {
            refaceDelay = mapClock + 3
        } else if (refaceDelay == mapClock) {
            faceSquare(path.face)
        }

        var cutCanoe = false

        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            faceSquare(path.face)
        } else if (actionDelay == mapClock) {
            val (low, high) = axeSuccessRates(axe, canoe_enums.station_axe_rates, enumTypes)
            cutCanoe = statRandom(stats.woodcutting, low, high, invisibleLvls)
        }

        if (!cutCanoe) {
            opLoc3(loc)
            return
        }

        // TODO(content): Degrade axe charges when applicable.

        val station = path.station
        this[station] = StationState.StationFalling
        resetAnim()
        soundSynth(synths.tree_fall_sound)
        worldRepo.locAnim(loc, canoe_seqs.canoeing_station_animations)
        delay(1)
        this[station] = StationState.StationReadyToShape
    }

    private fun ProtectedAccess.assertState(station: Station, state: StationState) {
        val varState = vars[station.stateVarBit]
        check(varState == state.varValue) {
            val actual = StationState.entries.firstOrNull { it.varValue == varState }
            "Unexpected state for station `$station`: expected=$state, actual=$actual ($varState)"
        }
    }

    private enum class Path(val station: Station, val dest: CoordGrid, val faceDir: Direction) {
        Lumbridge(
            station = Station.Lumbridge,
            dest = CoordGrid(0, 50, 50, 43, 35),
            faceDir = Direction.West,
        ),
        ChampionsGuild(
            station = Station.ChampionsGuild,
            dest = CoordGrid(0, 50, 52, 4, 15),
            faceDir = Direction.South,
        ),
        BarbarianVillage(
            station = Station.BarbarianVillage,
            dest = CoordGrid(0, 48, 53, 40, 17),
            faceDir = Direction.West,
        ),
        Edgeville(
            station = Station.Edgeville,
            dest = CoordGrid(0, 48, 54, 60, 52),
            faceDir = Direction.West,
        ),
        FeroxEnclave(
            station = Station.FeroxEnclave,
            dest = CoordGrid(0, 49, 56, 18, 48),
            faceDir = Direction.East,
        );

        val face: CoordGrid
            get() = dest.translate(faceDir)

        operator fun component1(): Station = station

        operator fun component2(): CoordGrid = dest

        operator fun component3(): CoordGrid = face
    }
}
