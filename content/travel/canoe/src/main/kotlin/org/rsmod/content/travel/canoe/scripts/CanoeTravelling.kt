package org.rsmod.content.travel.canoe.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.output.ClientScripts.highlightingOff
import org.rsmod.api.player.output.ClientScripts.highlightingOn
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.repo.region.RegionRepository
import org.rsmod.api.repo.region.RegionTemplate
import org.rsmod.api.script.onAiTimer
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.travel.canoe.configs.canoe_components
import org.rsmod.content.travel.canoe.configs.canoe_interfaces
import org.rsmod.content.travel.canoe.configs.canoe_locs
import org.rsmod.content.travel.canoe.configs.canoe_npcs
import org.rsmod.content.travel.canoe.configs.canoe_seqs
import org.rsmod.content.travel.canoe.configs.canoe_synths
import org.rsmod.content.travel.canoe.configs.canoe_varbits
import org.rsmod.game.entity.Npc
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocShape
import org.rsmod.game.map.Direction
import org.rsmod.game.region.Region
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CanoeTravelling @Inject private constructor(private val cutscene: CanoeCutscene) :
    PluginScript() {
    var ProtectedAccess.disabledPondWarning by boolVarBit(canoe_varbits.disable_wild_pond_warning)

    override fun ScriptContext.startup() {
        cutscene.startup(this)

        onOpLoc1(canoe_locs.floating_log) { openDestinationModal(Canoe.Log) }
        onOpLoc1(canoe_locs.floating_dugout) { openDestinationModal(Canoe.Dugout) }
        onOpLoc1(canoe_locs.floating_stable_dugout) { openDestinationModal(Canoe.StableDugout) }
        onOpLoc1(canoe_locs.floating_waka) { openDestinationModal(Canoe.Waka) }

        onIfModalButton(canoe_components.destination_lumbridge) {
            selectDestination(CanoeDestination.Lumbridge)
        }

        onIfModalButton(canoe_components.destination_champs_guild) {
            selectDestination(CanoeDestination.ChampionsGuild)
        }

        onIfModalButton(canoe_components.destination_barb_village) {
            selectDestination(CanoeDestination.BarbarianVillage)
        }

        onIfModalButton(canoe_components.destination_edgeville) {
            selectDestination(CanoeDestination.Edgeville)
        }

        onIfModalButton(canoe_components.destination_ferox_enclave) {
            selectDestination(CanoeDestination.FeroxEnclave)
        }

        onIfModalButton(canoe_components.destination_wild_pond) { selectWildernessPond() }
    }

    private fun ProtectedAccess.openDestinationModal(canoe: Canoe) {
        val station = resolveStation()
        val destinations = resolveValidDestinations(station, canoe)

        ifOpenMainModal(canoe_interfaces.destination, colour = 4535323, transparency = 0)
        for (destination in destinations) {
            ifSetEvents(destination.component, 0..0, IfEvent.Op1)
        }
    }

    private suspend fun ProtectedAccess.selectWildernessPond() {
        ifClose()

        if (disabledPondWarning) {
            travelTo(CanoeDestination.WildernessPond)
            return
        }

        mesbox(
            "<col=7f0000>Warning!</col> " +
                "This canoe will take you deep into the <col=7f0000>Wilderness</col>. " +
                "There are no trees suitable to make a canoe there. " +
                "You will have to walk back."
        )

        val confirmation =
            choice3(
                "Yes, I'm brave.",
                1,
                "Eeep! The Wilderness... No thank you.",
                2,
                "Yes, and don't show this warning again.",
                3,
                title = "Are you sure you wish to travel",
            )

        if (confirmation == 2) {
            mes("You decide not to travel to the Wilderness.")
            return
        }

        if (confirmation == 3) {
            disabledPondWarning = true
        }

        travelTo(CanoeDestination.WildernessPond)
    }

    private suspend fun ProtectedAccess.selectDestination(destination: CanoeDestination) {
        ifClose()
        travelTo(destination)
    }

    private suspend fun ProtectedAccess.travelTo(destination: CanoeDestination) {
        val station = resolveStation()
        val canoe =
            checkNotNull(canoeType) {
                "Expected valid canoe type: ${vars[canoe_varbits.canoe_type]}"
            }

        val destinations = resolveValidDestinations(station, canoe)
        check(destination in destinations) {
            "Destination should not be available: " +
                "destination=$destination, expected=$destinations, " +
                "canoe=$canoe, station=$station"
        }

        val destinationInWilderness = destination in CanoeDestination.wilderness
        val startingInWilderness = station.currentLocation() in CanoeDestination.wilderness
        val wilderness = destinationInWilderness || startingInWilderness

        val cutsceneType = if (wilderness) CutsceneType.Wilderness else CutsceneType.Grass
        cutscene.start(this, destination, cutsceneType)
    }

    private fun resolveValidDestinations(station: Station, canoe: Canoe): List<CanoeDestination> {
        val standard = CanoeDestination.standard
        val currentIndex = station.currentLocation().index
        val previous = standard.take(currentIndex).takeLast(canoe.maxStopCount())
        val next = standard.drop(currentIndex + 1).take(canoe.maxStopCount())
        val extra = if (canoe == Canoe.Waka) CanoeDestination.wilderness else emptyList()
        val filtered = (previous + next + extra) - station.currentLocation()
        return filtered.sortedBy(CanoeDestination::index)
    }

    private fun Station.currentLocation(): CanoeDestination =
        when (this) {
            Station.Lumbridge -> CanoeDestination.Lumbridge
            Station.ChampionsGuild -> CanoeDestination.ChampionsGuild
            Station.BarbarianVillage -> CanoeDestination.BarbarianVillage
            Station.Edgeville -> CanoeDestination.Edgeville
            Station.FeroxEnclave -> CanoeDestination.FeroxEnclave
        }

    private fun Canoe.maxStopCount(): Int =
        when (this) {
            Canoe.Log -> 1
            Canoe.Dugout -> 2
            Canoe.StableDugout -> 3
            Canoe.Waka -> 4
        }
}

private enum class CanoeDestination(val index: Int, val component: ComponentType) {
    Lumbridge(0, canoe_components.destination_lumbridge),
    ChampionsGuild(1, canoe_components.destination_champs_guild),
    BarbarianVillage(2, canoe_components.destination_barb_village),
    Edgeville(3, canoe_components.destination_edgeville),
    FeroxEnclave(4, canoe_components.destination_ferox_enclave),
    WildernessPond(5, canoe_components.destination_wild_pond);

    companion object {
        val wilderness = listOf(WildernessPond, FeroxEnclave)
        val standard = entries - wilderness
    }
}

private enum class CutsceneType {
    Grass,
    Wilderness,
}

private class CanoeCutscene
@Inject
constructor(
    private val regionRepo: RegionRepository,
    private val locRepo: LocRepository,
    private val npcRepo: NpcRepository,
    private val npcTypes: NpcTypeList,
) {
    fun startup(ctx: ScriptContext) {
        ctx.onAiTimer(canoe_npcs.cave_scenery_1) { npc.onSceneryTimer() }
        ctx.onAiTimer(canoe_npcs.cave_scenery_2) { npc.onSceneryTimer() }
        ctx.onAiTimer(canoe_npcs.cave_scenery_3) { npc.onSceneryTimer() }
        ctx.onAiTimer(canoe_npcs.tree_scenery_1) { npc.onSceneryTimer() }
        ctx.onAiTimer(canoe_npcs.tree_scenery_2) { npc.onSceneryTimer() }
        ctx.onAiTimer(canoe_npcs.bullrush_scenery_1) { npc.onSceneryTimer() }
        ctx.onAiTimer(canoe_npcs.bullrush_scenery_2) { npc.onSceneryTimer() }
    }

    suspend fun start(access: ProtectedAccess, dest: CanoeDestination, type: CutsceneType) {
        access.setUpCutscene()
        val region = createRegion(type)
        if (region == null) {
            access.closeCutscene()
            access.minimapReset()
            access.mes("Could not create region. Try again in a few seconds.")
            return
        }
        access.startCutscene(region, type, dest)
    }

    private fun Npc.onSceneryTimer() {
        walk(coords.translateZ(1))
        aiTimer(1)
    }

    private fun createRegion(type: CutsceneType): Region? {
        val template =
            when (type) {
                CutsceneType.Grass -> grassTemplate
                CutsceneType.Wilderness -> wildernessTemplate
            }
        return regionRepo.add(template)
    }

    private suspend fun ProtectedAccess.setUpCutscene() {
        fadeOverlay(
            startColour = 0,
            startTransparency = 255,
            endColour = 0,
            endTransparency = 0,
            clientDuration = 50,
        )
        clearHealthHud()
        delay(2)
        camModeClose()
        hideTopLevel()
        hideEntityOps()
        compassUnknown2()
        hideHealthHud()
        tempDisableAcceptAid()
        closeTopLevelTabsLenient()
        highlightingOff(player)
        minimapHideMap()
    }

    private fun ProtectedAccess.closeCutscene() {
        showEntityOps()
        restoreLastAcceptAid()
        compassReset()
        camModeReset()
        showTopLevel()
        showHealthHud()
        fadeOverlay(
            startColour = 0,
            startTransparency = 0,
            endColour = 0,
            endTransparency = 255,
            clientDuration = 50,
        )
        clearHealthHud()
        openTopLevelTabs()
        highlightingOn(player)
    }

    private suspend fun ProtectedAccess.startCutscene(
        region: Region,
        type: CutsceneType,
        dest: CanoeDestination,
    ) {
        val tripDuration: Int

        if (type == CutsceneType.Grass) {
            tripDuration = 22
            telejump(region.normal[0, 28, 70, 25, 35])
            rebuildAppearance()
            delay(1)
            anim(canoe_seqs.canoeing_rowing)
            faceDirection(Direction.West)
            soundSynth(canoe_synths.canoe_paddle, loops = 16)
            camMoveTo(region.normal[0, 28, 70, 20, 35], height = 2500, rate = 100, rate2 = 100)
            camMoveTo(region.normal[0, 28, 70, 20, 35], height = 255, rate = 100, rate2 = 100)
            camLookAt(region.normal[0, 28, 70, 25, 35], height = 255, rate = 100, rate2 = 100)
            spawnGrassScenery(region)
        } else {
            tripDuration = 11
            telejump(region.normal[0, 28, 70, 53, 12])
            rebuildAppearance()
            delay(1)
            anim(canoe_seqs.canoeing_rowing)
            faceDirection(Direction.West)
            soundSynth(canoe_synths.canoe_paddle, loops = 10)
            camMoveTo(region.normal[0, 28, 70, 46, 12], height = 275, rate = 100, rate2 = 100)
            camLookAt(region.normal[0, 28, 70, 53, 12], height = 100, rate = 100, rate2 = 100)
            spawnWildScenery(region)
            delay(1)
        }

        fadeOverlay(
            startColour = 0,
            startTransparency = 0,
            endColour = 0,
            endTransparency = 255,
            clientDuration = 50,
        )
        clearHealthHud()
        delay(tripDuration)
        fadeOverlay(
            startColour = 0,
            startTransparency = 255,
            endColour = 0,
            endTransparency = 0,
            clientDuration = 50,
        )
        clearHealthHud()
        delay(3)
        telejump(dest.arrivalCoords())
        resetAnim()
        camReset()
        delay(1)
        closeCutscene()
        spawnSinkingCanoe(dest)
        minimapReset()
        rebuildAppearance()
        spam(dest.arrivalMessage())
        clearCanoeVars()
        closeFadeOverlay()
    }

    private fun spawnGrassScenery(region: Region) {
        val npcs = constructGrassSceneryNpcs(region)
        spawnScenery(npcs)
    }

    private fun spawnWildScenery(region: Region) {
        val npcs = constructWildernessSceneryNpcs(region)
        spawnScenery(npcs)
    }

    private fun spawnScenery(npcs: List<SceneryNpc>) {
        for (scenery in npcs) {
            val (type, coords, duration, delay) = scenery
            val npc = Npc(npcTypes[type], coords)
            npcRepo.add(npc, duration)
            if (delay != null) {
                npcRepo.hide(npc, delay)
            }
        }
    }

    private fun ProtectedAccess.spawnSinkingCanoe(dest: CanoeDestination) {
        val canoe = canoeType
        if (canoe != null) {
            val coords = dest.sinkingCanoeCoords()
            val angle =
                if (dest == CanoeDestination.WildernessPond) LocAngle.East else LocAngle.North
            val sinking = lcParam(canoe.loc, params.next_loc_stage)
            locRepo.add(coords, sinking, 4, angle, LocShape.CentrepieceStraight)
        }
        soundSynth(canoe_synths.canoe_sink)
    }

    private fun CanoeDestination.arrivalMessage(): String =
        when (this) {
            CanoeDestination.Lumbridge -> {
                "You arrive at Lumbridge." +
                    "<br>Your canoe sinks into the water after the hard journey."
            }
            CanoeDestination.ChampionsGuild -> {
                "You arrive at the Champions' Guild." +
                    "<br>Your canoe sinks into the water after the hard journey."
            }
            CanoeDestination.BarbarianVillage -> {
                "You arrive at the Barbarian Village." +
                    "<br>Your canoe sinks into the water after the hard journey."
            }
            CanoeDestination.Edgeville -> {
                "You arrive at Edgeville." +
                    "<br>Your canoe sinks into the water after the hard journey."
            }
            CanoeDestination.FeroxEnclave -> {
                "You arrive at Ferox Enclave." +
                    "<br>Your canoe sinks into the water after the hard journey."
            }
            CanoeDestination.WildernessPond -> {
                "You arrive in the Wilderness. There are no trees suitable to make a canoe.<br>" +
                    "Your canoe sinks into the water after the hard journey. " +
                    "Looks like you're walking back."
            }
        }

    private fun CanoeDestination.arrivalCoords(): CoordGrid =
        when (this) {
            CanoeDestination.Lumbridge -> CoordGrid(0, 50, 50, 40, 42)
            CanoeDestination.ChampionsGuild -> CoordGrid(0, 49, 52, 63, 16)
            CanoeDestination.BarbarianVillage -> CoordGrid(0, 48, 53, 37, 23)
            CanoeDestination.Edgeville -> CoordGrid(0, 48, 54, 56, 47)
            CanoeDestination.FeroxEnclave -> CoordGrid(0, 49, 56, 18, 54)
            CanoeDestination.WildernessPond -> CoordGrid(0, 49, 59, 5, 20)
        }

    private fun CanoeDestination.sinkingCanoeCoords(): CoordGrid =
        when (this) {
            CanoeDestination.Lumbridge -> CoordGrid(0, 50, 50, 39, 40)
            CanoeDestination.ChampionsGuild -> CoordGrid(0, 49, 52, 61, 14)
            CanoeDestination.BarbarianVillage -> CoordGrid(0, 48, 53, 37, 19)
            CanoeDestination.Edgeville -> CoordGrid(0, 48, 54, 55, 48)
            CanoeDestination.FeroxEnclave -> CoordGrid(0, 49, 56, 21, 52)
            CanoeDestination.WildernessPond -> CoordGrid(0, 49, 59, 6, 19)
        }

    private fun constructGrassSceneryNpcs(region: Region): List<SceneryNpc> {
        val npcs = canoe_npcs
        return listOf(
            SceneryNpc(npcs.bullrush_scenery_1, region.normal[0, 28, 70, 28, 36], duration = 7),
            SceneryNpc(npcs.bullrush_scenery_2, region.normal[0, 28, 70, 27, 36], duration = 7),
            SceneryNpc(npcs.tree_scenery_1, region.normal[0, 28, 70, 30, 36], duration = 7),
            SceneryNpc(npcs.tree_scenery_2, region.normal[0, 28, 70, 29, 23], duration = 21),
            SceneryNpc(
                type = npcs.tree_scenery_1,
                coords = region.normal[0, 28, 70, 29, 19],
                duration = 25,
                delay = 7,
            ),
            SceneryNpc(
                type = npcs.bullrush_scenery_2,
                coords = region.normal[0, 28, 70, 27, 24],
                duration = 25,
                delay = 10,
            ),
            SceneryNpc(
                type = npcs.bullrush_scenery_1,
                coords = region.normal[0, 28, 70, 28, 20],
                duration = 25,
                delay = 10,
            ),
        )
    }

    private fun constructWildernessSceneryNpcs(region: Region): List<SceneryNpc> {
        val npcs = canoe_npcs
        return listOf(
            SceneryNpc(
                type = npcs.cave_scenery_1,
                coords = region.normal[0, 28, 70, 50, 9],
                duration = 14,
            ),
            SceneryNpc(
                type = npcs.cave_scenery_2,
                coords = region.normal[0, 28, 70, 54, 8],
                duration = 14,
                delay = 1,
            ),
            SceneryNpc(
                type = npcs.cave_scenery_1,
                coords = region.normal[0, 28, 70, 50, 2],
                duration = 14,
                delay = 2,
            ),
            SceneryNpc(
                type = npcs.cave_scenery_2,
                coords = region.normal[0, 28, 70, 54, 4],
                duration = 14,
                delay = 3,
            ),
            SceneryNpc(
                type = npcs.cave_scenery_3,
                coords = region.normal[0, 28, 70, 54, 0],
                duration = 14,
                delay = 5,
            ),
        )
    }

    private data class SceneryNpc(
        val type: NpcType,
        val coords: CoordGrid,
        val duration: Int,
        val delay: Int? = null,
    )

    companion object {
        private val grassTemplate =
            RegionTemplate.create {
                copy(225, 562, 0) {
                    zoneWidth = 4
                    zoneLength = 4
                }
            }

        private val wildernessTemplate =
            RegionTemplate.create {
                copy(229, 560, 0) {
                    zoneWidth = 3
                    zoneLength = 4
                }
            }
    }
}
