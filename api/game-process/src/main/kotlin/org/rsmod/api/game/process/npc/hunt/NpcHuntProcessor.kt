package org.rsmod.api.game.process.npc.hunt

import jakarta.inject.Inject
import org.rsmod.api.hunt.Hunt
import org.rsmod.api.npc.apLoc1
import org.rsmod.api.npc.apLoc2
import org.rsmod.api.npc.apLoc3
import org.rsmod.api.npc.apLoc4
import org.rsmod.api.npc.apLoc5
import org.rsmod.api.npc.apNpc1
import org.rsmod.api.npc.apNpc2
import org.rsmod.api.npc.apNpc3
import org.rsmod.api.npc.apNpc4
import org.rsmod.api.npc.apNpc5
import org.rsmod.api.npc.apObj1
import org.rsmod.api.npc.apObj2
import org.rsmod.api.npc.apObj3
import org.rsmod.api.npc.apObj4
import org.rsmod.api.npc.apObj5
import org.rsmod.api.npc.apPlayer1
import org.rsmod.api.npc.apPlayer2
import org.rsmod.api.npc.apPlayer3
import org.rsmod.api.npc.apPlayer4
import org.rsmod.api.npc.apPlayer5
import org.rsmod.api.npc.interact.AiLocInteractions
import org.rsmod.api.npc.interact.AiNpcInteractions
import org.rsmod.api.npc.interact.AiObjInteractions
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.npc.opLoc1
import org.rsmod.api.npc.opLoc2
import org.rsmod.api.npc.opLoc3
import org.rsmod.api.npc.opLoc4
import org.rsmod.api.npc.opLoc5
import org.rsmod.api.npc.opNpc1
import org.rsmod.api.npc.opNpc2
import org.rsmod.api.npc.opNpc3
import org.rsmod.api.npc.opNpc4
import org.rsmod.api.npc.opNpc5
import org.rsmod.api.npc.opObj1
import org.rsmod.api.npc.opObj2
import org.rsmod.api.npc.opObj3
import org.rsmod.api.npc.opObj4
import org.rsmod.api.npc.opObj5
import org.rsmod.api.npc.opPlayer1
import org.rsmod.api.npc.opPlayer2
import org.rsmod.api.npc.opPlayer3
import org.rsmod.api.npc.opPlayer4
import org.rsmod.api.npc.opPlayer5
import org.rsmod.api.random.CoreRandom
import org.rsmod.api.random.GameRandom
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.hunt.HuntModeTypeList
import org.rsmod.game.type.hunt.HuntNobodyNear
import org.rsmod.game.type.hunt.HuntType
import org.rsmod.game.type.hunt.UnpackedHuntModeType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.varn.VarnTypeList
import org.rsmod.game.type.varp.VarpTypeList

public class NpcHuntProcessor
@Inject
constructor(
    @CoreRandom private val random: GameRandom,
    private val mapClock: MapClock,
    private val hunt: Hunt,
    private val huntModes: HuntModeTypeList,
    private val varpTypes: VarpTypeList,
    private val varnTypes: VarnTypeList,
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val playerList: PlayerList,
    private val npcList: NpcList,
    private val playerInteractions: AiPlayerInteractions,
    private val npcInteractions: AiNpcInteractions,
    private val objInteractions: AiObjInteractions,
    private val locInteractions: AiLocInteractions,
) {
    public fun process(npc: Npc) {
        if (!npc.isValidTarget() || npc.isDelayed) {
            return
        }
        val huntType = huntModes[npc.huntMode] ?: return
        npc.nonPlayerHunt(huntType)
        npc.incrementHuntClock(huntType)
        npc.consumeHuntTarget(huntType)
    }

    private fun Npc.nonPlayerHunt(mode: UnpackedHuntModeType) {
        val huntDisabled = huntRange == 0
        if (huntDisabled) {
            return
        }

        val skipHunt = mode.type == HuntType.Off || mode.type == HuntType.Player
        if (skipHunt) {
            return
        }

        val isBusy = hasInteraction()
        if (isBusy) {
            return
        }

        val pauseHunt = mode.nobodyNear == HuntNobodyNear.PauseHunt && !isAnyoneNear()
        if (pauseHunt) {
            return
        }

        val huntDelayed = huntClock < mode.rate - 1
        if (huntDelayed) {
            return
        }

        hunt(mode)
    }

    private fun Npc.incrementHuntClock(mode: UnpackedHuntModeType) {
        val playerHunt = mode.type == HuntType.Player
        if (playerHunt) {
            huntClock++
            return
        }

        val pauseHunt = mode.nobodyNear == HuntNobodyNear.PauseHunt && !isAnyoneNear()
        if (!pauseHunt) {
            huntClock++
        }
    }

    private fun Npc.hunt(mode: UnpackedHuntModeType) {
        when (mode.type) {
            HuntType.Off -> {}
            HuntType.Player -> error("Npc player hunt should occur earlier in the cycle.")
            HuntType.Npc -> huntNpc(mode)
            HuntType.Obj -> huntObj(mode)
            HuntType.Scenery -> huntLoc(mode)
        }
    }

    private fun Npc.huntNpc(mode: UnpackedHuntModeType) {
        var target = NpcUid.NULL
        var count = 0

        val npcs = hunt.findNpcs(coords, huntRange, mode.checkVis)
        for (npc in npcs) {
            val check = mode.checkNpc
            if (check != null) {
                if (check.npc != null && check.npc != npc.id) {
                    continue
                }
                if (check.category != null && check.npc != npc.type.category) {
                    continue
                }
            }
            count++
            if (random.of(minInclusive = 0, maxInclusive = count) == 0) {
                target = npc.uid
            }
        }

        if (target != NpcUid.NULL) {
            huntNpc = target
        }
    }

    private fun Npc.huntObj(mode: UnpackedHuntModeType) {
        var target: Obj? = null
        var count = 0

        val objs = hunt.findObjs(coords, huntRange, mode.checkVis)
        for (obj in objs) {
            val check = mode.checkObj
            if (check != null) {
                if (check.obj != null && check.obj != obj.type) {
                    continue
                }
                val objType = objTypes.getValue(obj.type)
                if (check.category != null && check.obj != objType.category) {
                    continue
                }
            }
            count++
            if (random.of(minInclusive = 0, maxInclusive = count) == 0) {
                target = obj
            }
        }

        if (target != null) {
            huntObj = target
        }
    }

    private fun Npc.huntLoc(mode: UnpackedHuntModeType) {
        var target: LocInfo? = null
        var count = 0

        val locs = hunt.findLocs(coords, huntRange, mode.checkVis)
        for (loc in locs) {
            val check = mode.checkLoc
            if (check != null) {
                if (check.loc != null && check.loc != loc.id) {
                    continue
                }
                val locType = locTypes.getValue(loc.id)
                if (check.category != null && check.loc != locType.category) {
                    continue
                }
            }
            count++
            if (random.of(minInclusive = 0, maxInclusive = count) == 0) {
                target = loc
            }
        }

        if (target != null) {
            huntLoc = target
        }
    }

    private fun Npc.consumeHuntTarget(mode: UnpackedHuntModeType) {
        var consumedTarget = false
        when (mode.type) {
            HuntType.Off -> {
                return
            }

            HuntType.Player -> {
                val target = huntPlayer.resolve(playerList)
                if (target != null) {
                    consumedTarget = true
                    consumePlayerTarget(target, mode.findNewMode)
                }
            }

            HuntType.Npc -> {
                val target = huntNpc.resolve(npcList)
                if (target != null) {
                    consumedTarget = true
                    consumeNpcTarget(target, mode.findNewMode)
                }
            }

            HuntType.Obj -> {
                val target = huntObj
                if (target != null) {
                    consumedTarget = true
                    consumeObjTarget(target, mode.findNewMode)
                }
            }

            HuntType.Scenery -> {
                val target = huntLoc
                if (target != null) {
                    consumedTarget = true
                    val boundLoc = BoundLocInfo(target, locTypes[target])
                    consumeLocTarget(boundLoc, mode.findNewMode)
                }
            }
        }

        if (!consumedTarget) {
            return
        }

        resetHunt()
        if (!mode.findKeepHunting) {
            setHunt(0)
        }
    }

    private fun Npc.consumePlayerTarget(target: Player, mode: NpcMode) {
        when (mode) {
            NpcMode.OpPlayer1 -> opPlayer1(target, playerInteractions)
            NpcMode.OpPlayer2 -> opPlayer2(target, playerInteractions)
            NpcMode.OpPlayer3 -> opPlayer3(target, playerInteractions)
            NpcMode.OpPlayer4 -> opPlayer4(target, playerInteractions)
            NpcMode.OpPlayer5 -> opPlayer5(target, playerInteractions)
            NpcMode.ApPlayer1 -> apPlayer1(target, playerInteractions)
            NpcMode.ApPlayer2 -> apPlayer2(target, playerInteractions)
            NpcMode.ApPlayer3 -> apPlayer3(target, playerInteractions)
            NpcMode.ApPlayer4 -> apPlayer4(target, playerInteractions)
            NpcMode.ApPlayer5 -> apPlayer5(target, playerInteractions)
            else -> throw NotImplementedError("Unhandled npc mode hunt: $mode")
        }
    }

    private fun Npc.consumeNpcTarget(target: Npc, mode: NpcMode) {
        when (mode) {
            NpcMode.OpNpc1 -> opNpc1(target, npcInteractions)
            NpcMode.OpNpc2 -> opNpc2(target, npcInteractions)
            NpcMode.OpNpc3 -> opNpc3(target, npcInteractions)
            NpcMode.OpNpc4 -> opNpc4(target, npcInteractions)
            NpcMode.OpNpc5 -> opNpc5(target, npcInteractions)
            NpcMode.ApNpc1 -> apNpc1(target, npcInteractions)
            NpcMode.ApNpc2 -> apNpc2(target, npcInteractions)
            NpcMode.ApNpc3 -> apNpc3(target, npcInteractions)
            NpcMode.ApNpc4 -> apNpc4(target, npcInteractions)
            NpcMode.ApNpc5 -> apNpc5(target, npcInteractions)
            else -> throw NotImplementedError("Unhandled npc mode hunt: $mode")
        }
    }

    private fun Npc.consumeObjTarget(target: Obj, mode: NpcMode) {
        when (mode) {
            NpcMode.OpObj1 -> opObj1(target, objInteractions)
            NpcMode.OpObj2 -> opObj2(target, objInteractions)
            NpcMode.OpObj3 -> opObj3(target, objInteractions)
            NpcMode.OpObj4 -> opObj4(target, objInteractions)
            NpcMode.OpObj5 -> opObj5(target, objInteractions)
            NpcMode.ApObj1 -> apObj1(target, objInteractions)
            NpcMode.ApObj2 -> apObj2(target, objInteractions)
            NpcMode.ApObj3 -> apObj3(target, objInteractions)
            NpcMode.ApObj4 -> apObj4(target, objInteractions)
            NpcMode.ApObj5 -> apObj5(target, objInteractions)
            else -> throw NotImplementedError("Unhandled npc mode hunt: $mode")
        }
    }

    private fun Npc.consumeLocTarget(target: BoundLocInfo, mode: NpcMode) {
        when (mode) {
            NpcMode.OpLoc1 -> opLoc1(target, locInteractions)
            NpcMode.OpLoc2 -> opLoc2(target, locInteractions)
            NpcMode.OpLoc3 -> opLoc3(target, locInteractions)
            NpcMode.OpLoc4 -> opLoc4(target, locInteractions)
            NpcMode.OpLoc5 -> opLoc5(target, locInteractions)
            NpcMode.ApLoc1 -> apLoc1(target, locInteractions)
            NpcMode.ApLoc2 -> apLoc2(target, locInteractions)
            NpcMode.ApLoc3 -> apLoc3(target, locInteractions)
            NpcMode.ApLoc4 -> apLoc4(target, locInteractions)
            NpcMode.ApLoc5 -> apLoc5(target, locInteractions)
            else -> throw NotImplementedError("Unhandled npc mode hunt: $mode")
        }
    }
}
