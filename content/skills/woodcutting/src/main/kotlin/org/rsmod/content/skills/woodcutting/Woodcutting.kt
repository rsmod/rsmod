package org.rsmod.content.skills.woodcutting

import jakarta.inject.Inject
import org.rsmod.api.config.Constants
import org.rsmod.api.config.locParam
import org.rsmod.api.config.locXpParam
import org.rsmod.api.config.objParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.controllers
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varcons
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.woodcuttingLvl
import org.rsmod.api.player.vars.intVarCon
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.controller.ControllerRepository
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.player.PlayerRepository
import org.rsmod.api.script.onAiConTimer
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc3
import org.rsmod.api.xpmod.XpModifiers
import org.rsmod.events.UnboundEvent
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.map.zone.ZoneKey
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// TODO:
// - bird nests
// - axe effects/charges
class Woodcutting
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val locRepo: LocRepository,
    private val conRepo: ControllerRepository,
    private val playerRepo: PlayerRepository,
    private val mods: XpModifiers,
    private val mapClock: MapClock,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.tree) { attempt(it.bound, it.type) }
        onOpLoc3(content.tree) { cut(it.bound, it.type) }
        onAiConTimer(controllers.woodcutting_tree_duration) { controller.treeDespawnTick() }
    }

    private fun ProtectedAccess.attempt(tree: BoundLocInfo, type: UnpackedLocType) {
        if (player.woodcuttingLvl < type.treeLevelReq) {
            mes("You need a Woodcutting level of ${type.treeLevelReq} to chop down this tree.")
            return
        }

        if (inv.isFull()) {
            val product = objTypes[type.treeLogs]
            mes("Your inventory is too full to hold any more ${product.name.lowercase()}.")
            soundSynth(synths.pillory_wrong)
            return
        }

        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
            skillAnimDelay = mapClock + 3
            opLoc1(tree)
        } else {
            val axe = findAxe(player, objTypes)
            if (axe == null) {
                mes("You need an axe to chop down this tree.")
                mes("You do not have an axe which you have the woodcutting level to use.")
                return
            }
            anim(objTypes[axe].axeWoodcuttingAnim)
            spam("You swing your axe at the tree.")
            cut(tree, type)
        }
    }

    private fun ProtectedAccess.cut(tree: BoundLocInfo, type: UnpackedLocType) {
        val axe = findAxe(player, objTypes)
        if (axe == null) {
            mes("You need an axe to chop down this tree.")
            mes("You do not have an axe which you have the woodcutting level to use.")
            return
        }

        if (player.woodcuttingLvl < type.treeLevelReq) {
            mes("You need a Woodcutting level of ${type.treeLevelReq} to chop down this tree.")
            return
        }

        if (inv.isFull()) {
            val product = objTypes[type.treeLogs]
            mes("Your inventory is too full to hold any more ${product.name.lowercase()}.")
            soundSynth(synths.pillory_wrong)
            return
        }

        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + 4
            anim(objTypes[axe].axeWoodcuttingAnim)
        }

        var cutLogs = false
        var despawn = false

        if (actionDelay < mapClock) {
            actionDelay = mapClock + 3
        } else if (actionDelay == mapClock) {
            cutLogs = true // TODO: Random roll
            despawn = !type.hasDespawnTimer && random.of(1, 255) > type.treeDepleteChance
        }

        if (type.hasDespawnTimer) {
            treeSwingDespawnTick(tree, type)
            despawn = cutLogs && isTreeDespawnRequired(tree)
        }

        if (cutLogs) {
            val product = objTypes[type.treeLogs]
            val xp = type.treeXp * mods.get(player, stats.woodcutting)
            spam("You get some ${product.name.lowercase()}.")
            statAdvance(stats.woodcutting, xp)
            invAdd(inv, product)
            publish(CutLogs(player, tree, product))
        }

        if (despawn) {
            val respawnTime = type.resolveRespawnTime(random)
            locRepo.change(tree, type.treeStump, respawnTime)
            resetAnim()
            soundSynth(synths.tree_fall_sound)
            sendLocalOverlayLoc(tree, type, respawnTime)
            return
        }

        opLoc3(tree)
    }

    private fun Controller.treeDespawnTick() {
        val tree = locRepo.findExact(coords, locTypes[treeLocId])
        if (tree == null) {
            // Make sure the controller has lived beyond a single tick. Otherwise, we can make an
            // educated guess that there's an oversight allowing the tree to recreate controllers
            // faster than we'd expect. (1 tick intervals in this case)
            check(mapClock > creationCycle + 1) { "Tree loc deleted faster than expected." }
            conRepo.del(this)
            return
        }

        // If tree is actively being cut down by a player, increment the associated varcon.
        if (treeLastCut == mapClock.cycle - 1) {
            treeActivelyCutTicks++
        } else {
            treeActivelyCutTicks--
        }

        // If the tree has been idle (not cut) for a duration equal to or longer than the time it
        // was actively cut, the controller is no longer needed and can be safely deleted.
        if (treeActivelyCutTicks <= 0) {
            conRepo.del(this)
            return
        }

        // Reset the timer for next tick.
        aiTimer(1)

        // Keep the controller alive.
        resetDuration()
    }

    private fun treeSwingDespawnTick(tree: BoundLocInfo, type: UnpackedLocType) {
        val controller = conRepo.findExact(tree.coords, controllers.woodcutting_tree_duration)
        if (controller != null) {
            check(controller.treeLocId == tree.id) {
                "Controller in coords is not associated with tree: " +
                    "controller=$controller, treeLoc=$tree, treeType=$type"
            }
            controller.treeLastCut = mapClock.cycle
            return
        }

        val spawn = Controller(tree.coords, controllers.woodcutting_tree_duration)
        conRepo.add(spawn, type.treeDespawnTime)

        spawn.treeLocId = tree.id
        spawn.treeLastCut = mapClock.cycle
        spawn.treeActivelyCutTicks = 0
        spawn.aiTimer(1)
    }

    private fun isTreeDespawnRequired(tree: BoundLocInfo): Boolean {
        val controller = conRepo.findExact(tree.coords, controllers.woodcutting_tree_duration)
        return controller != null && controller.treeActivelyCutTicks >= controller.durationStart
    }

    private fun sendLocalOverlayLoc(tree: BoundLocInfo, type: UnpackedLocType, respawnTime: Int) {
        val players = playerRepo.findAll(ZoneKey.from(tree.coords), zoneRadius = 3)
        for (player in players) {
            ClientScripts.addOverlayLoc(
                player = player,
                coords = tree.coords,
                loc = type,
                shape = tree.shape,
                timer = Constants.overlay_timer_woodcutting,
                ticks = respawnTime,
                colour = 16765184,
                unknownInt = 0,
            )
        }
    }

    data class CutLogs(val player: Player, val tree: BoundLocInfo, val product: ObjType) :
        UnboundEvent

    companion object {
        var Controller.treeActivelyCutTicks: Int by intVarCon(varcons.woodcutting_tree_cut_ticks)
        var Controller.treeLastCut: Int by intVarCon(varcons.woodcutting_tree_last_cut)
        var Controller.treeLocId: Int by intVarCon(varcons.woodcutting_tree_loc)

        val UnpackedObjType.axeWoodcuttingReq: Int by objParam(params.skill_levelreq)
        val UnpackedObjType.axeWoodcuttingAnim: SeqType by objParam(params.skill_anim)

        val UnpackedLocType.treeLevelReq: Int by locParam(params.skill_levelreq)
        val UnpackedLocType.treeLogs: ObjType by locParam(params.skill_productitem)
        val UnpackedLocType.treeXp: Double by locXpParam(params.skill_xp)
        val UnpackedLocType.treeStump: LocType by locParam(params.next_loc_stage)
        val UnpackedLocType.treeDespawnTime: Int by locParam(params.despawn_time)
        val UnpackedLocType.treeDepleteChance: Int by locParam(params.deplete_chance)
        val UnpackedLocType.treeRespawnTime: Int by locParam(params.respawn_time)
        val UnpackedLocType.treeRespawnTimeLow: Int by locParam(params.respawn_time_low)
        val UnpackedLocType.treeRespawnTimeHigh: Int by locParam(params.respawn_time_high)

        private val UnpackedLocType.hasDespawnTimer: Boolean
            get() = hasParam(params.despawn_time)

        fun findAxe(player: Player, objTypes: ObjTypeList): InvObj? {
            val worn = player.wornAxe(objTypes)
            val carried = player.carriedAxe(objTypes)
            if (worn != null && carried != null) {
                if (objTypes[worn].axeWoodcuttingReq >= objTypes[carried].axeWoodcuttingReq) {
                    return worn
                }
                return carried
            }
            return worn ?: carried
        }

        private fun Player.wornAxe(objTypes: ObjTypeList): InvObj? {
            return righthand?.let {
                val type = objTypes[it]
                if (type.isUsableAxe(woodcuttingLvl)) {
                    it
                } else {
                    null
                }
            }
        }

        private fun Player.carriedAxe(objTypes: ObjTypeList): InvObj? {
            return inv.filterNotNull { objTypes[it].isUsableAxe(woodcuttingLvl) }
                .maxByOrNull { objTypes[it].axeWoodcuttingReq }
        }

        private fun UnpackedObjType.isUsableAxe(woodcuttingLevel: Int): Boolean =
            isAssociatedWith(content.woodcutting_axe) && woodcuttingLevel >= axeWoodcuttingReq

        private fun UnpackedLocType.resolveRespawnTime(random: GameRandom): Int {
            val fixed = treeRespawnTime
            if (fixed > 0) {
                return fixed
            }
            return random.of(treeRespawnTimeLow, treeRespawnTimeHigh)
        }
    }
}
