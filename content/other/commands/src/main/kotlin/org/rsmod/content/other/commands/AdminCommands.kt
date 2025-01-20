package org.rsmod.content.other.commands

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.cheat.CheatHandlerBuilder
import org.rsmod.api.config.refs.modlevels
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invClear
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.script.onCommand
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.game.cheat.Cheat
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.stat.PlayerSkillXPTable
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.routefinder.loc.LocLayerConstants

class AdminCommands
@Inject
constructor(
    private val protectedAccess: ProtectedAccessLauncher,
    private val statTypes: StatTypeList,
    private val seqTypes: SeqTypeList,
    private val locTypes: LocTypeList,
    private val npcTypes: NpcTypeList,
    private val objTypes: ObjTypeList,
    private val locRepo: LocRepository,
    private val npcRepo: NpcRepository,
    private val names: NameMapping,
) : PluginScript() {
    private val logger = InlineLogger()

    override fun ScriptContext.startUp() {
        onCommand("master", "Max out all stats", ::master)
        onCommand("reset", "Reset all stats", ::reset)
        onCommand("mypos", "Get current coordinates", ::mypos)
        onCommand("tele", "Teleport to coordgrid", ::tele) {
            invalidArgs = "Use as ::tele level mx mz lx lz (ex: 0 50 50 0 0)"
        }
        onCommand("anim", "Play animation", ::anim)
        onCommand("locadd", "Spawn loc", ::locAdd) {
            invalidArgs = "Use as ::locadd duration locDebugNameOrId (ex: 100 bookcase)"
        }
        onCommand("locdel", "Remove loc", ::locDel) { invalidArgs = "Use as ::locdel duration" }
        onCommand("npcadd", "Spawn npc", ::npcAdd) {
            invalidArgs = "Use as ::npcadd duration npcDebugNameOrId (ex: 100 prison_pete)"
        }
        onCommand("invadd", "Spawn obj into inv", ::invAdd)
        onCommand("invclear", "Remove all objs from inv", ::invClear)
    }

    private fun master(cheat: Cheat) = with(cheat) { player.setStatLevels(level = 99) }

    private fun reset(cheat: Cheat) = with(cheat) { player.setStatLevels(level = 1) }

    private fun mypos(cheat: Cheat) =
        with(cheat) {
            player.mes("${player.coords}:")
            player.mes("  ${ZoneKey.from(player.coords)} - ${ZoneGrid.from(player.coords)}")
            player.mes(
                "  ${MapSquareKey.from(player.coords)} - ${MapSquareGrid.from(player.coords)}"
            )
            player.mes("  BuildArea(${player.buildArea})")
        }

    private fun tele(cheat: Cheat) =
        with(cheat) {
            val args = if (args.size == 1) args[0].split(",") else args
            val level = args[0].toInt()
            val mx = args[1].toInt()
            val mz = args[2].toInt()
            val lx = args[3].toInt()
            val lz = args[4].toInt()
            val coords = CoordGrid(level, mx, mz, lx, lz)
            protectedAccess.launch(player) {
                telejump(coords)
                player.mes("Teleported to $coords.")
            }
        }

    private fun anim(cheat: Cheat) =
        with(cheat) {
            val typeId = resolveArgTypeId(args[0], names.seqs)
            if (typeId == null) {
                player.mes("There is no seq mapped to: `${args[0]}`")
                return
            }
            val type = seqTypes[typeId]
            if (type == null) {
                player.mes("That seq does not exist: $typeId")
                return
            }
            player.anim(type)
            player.mes("Anim: ${type.id} (priority=${type.priority})")
            logger.debug { "Anim: $type" }
        }

    private fun locAdd(cheat: Cheat) =
        with(cheat) {
            val typeId = resolveArgTypeId(args[1], names.locs)
            if (typeId == null) {
                player.mes("There is no loc mapped to name: `${args[0]}`")
                return
            }
            val type = locTypes[typeId]
            if (type == null) {
                player.mes("That loc does not exist: $typeId")
                return
            }
            val duration = args[0].toInt()
            val angle = args.getOrNull(2)?.toInt() ?: LocAngle.West.id
            val shape = args.getOrNull(3)?.toInt() ?: LocShape.CentrepieceStraight.id
            val layer = LocLayerConstants.of(shape)
            val loc = LocInfo(layer, player.coords, LocEntity(type.id, shape, angle))
            locRepo.add(loc, duration)
            player.mes("Spawned loc `${type.internalNameGet}` (duration: $duration cycles)")
            logger.debug { "Spawned loc: loc=$loc, type=$type" }
        }

    private fun locDel(cheat: Cheat) =
        with(cheat) {
            val zone = ZoneKey.from(player.coords)
            val locs = locRepo.findAll(zone).filter { it.coords == player.coords }.toList()
            if (locs.isEmpty()) {
                player.mes("No loc found on ${player.coords}")
                return
            }
            val duration = args[0].toInt()
            val shape = args.getOrNull(1)?.toIntOrNull() ?: LocShape.CentrepieceStraight.id
            val loc = locs.firstOrNull { it.shapeId == shape }
            if (loc == null) {
                player.mes("No loc with shape `${LocShape[shape]}` found on ${player.coords}")
                return
            }
            val type = locTypes[loc]
            locRepo.del(loc, duration)
            player.mes("Deleted loc `${type.internalNameGet}` (duration: $duration cycles)")
            logger.debug { "Deleted loc: loc=$loc, type=$type" }
        }

    private fun npcAdd(cheat: Cheat) =
        with(cheat) {
            val typeId = resolveArgTypeId(args[1], names.npcs)
            if (typeId == null) {
                player.mes("There is no npc mapped to name: `${args[0]}`")
                return
            }
            val type = npcTypes[typeId]
            if (type == null) {
                player.mes("That npc does not exist: $typeId")
                return
            }
            val duration = args[0].toInt()
            val npc = Npc(type, player.coords)
            npc.mode = NpcMode.None
            npcRepo.add(npc, duration)
            player.mes("Spawned npc `${type.internalNameGet}` (duration: $duration cycles)")
        }

    private fun invAdd(cheat: Cheat) =
        with(cheat) {
            val (typeName, countArg) =
                if (args.size > 1 && args.last().toLongOrNull() != null) {
                    args.dropLast(1).joinToString("_") to args.last()
                } else {
                    args.joinToString("_") to "1"
                }
            val resolvedName = typeName.replace("cert_", "")
            val typeId = resolveArgTypeId(resolvedName, names.objs)
            if (typeId == null) {
                player.mes("There is no obj mapped to name: `$resolvedName`")
                return
            }
            val type = objTypes[typeId]
            if (type == null) {
                player.mes("That obj does not exist: $typeId")
                return
            }
            val spawnCert = typeName.startsWith("cert_")
            val resolvedType =
                if (spawnCert && type.canCert) objTypes.getValue(type.certlink) else type
            val count = countArg.toLong().coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            val objName = type.internalNameGet ?: type.name
            val spawned = player.invAdd(player.inv, resolvedType, count, strict = false)
            player.mes("Spawned inv obj `$objName` x ${spawned.completed().formatAmount}")
        }

    private fun invClear(cheat: Cheat) = with(cheat) { player.invClear(player.inv) }

    private fun Player.setStatLevels(level: Int) {
        val xp = PlayerSkillXPTable.getXPFromLevel(level)
        for (stat in statTypes.values) {
            statMap.setXP(stat, xp)
            statMap.setBaseLevel(stat, level.toByte())
            statMap.setCurrentLevel(stat, level.toByte())
            UpdateStat.update(this, stat, xp, level, level)
        }
    }

    private fun resolveArgTypeId(arg: String, names: Map<String, Int>): Int? {
        val argAsInt = arg.toIntOrNull()
        if (argAsInt != null) {
            return argAsInt
        }
        val sanitized = arg.replace("-", "_")
        return names[sanitized]
    }

    private fun ScriptContext.onCommand(
        command: String,
        desc: String,
        cheat: Cheat.() -> Unit,
        init: CheatHandlerBuilder.() -> Unit = {},
    ) {
        onCommand(command) {
            this.modLevel = modlevels.admin
            this.desc = desc
            this.cheat(cheat)
            init()
        }
    }
}
