package org.runerealm.api.game.process.npc.hunt

import jakarta.inject.Inject
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.hunt.Hunt
import org.runerealm.api.npc.isValidTarget
import org.runerealm.api.random.CoreRandom
import org.runerealm.api.random.GameRandom
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.PlayerUid
import org.runerealm.game.type.hunt.HuntCheckNotTooStrong
import org.runerealm.game.type.hunt.HuntModeTypeList
import org.runerealm.game.type.hunt.HuntType
import org.runerealm.game.type.hunt.UnpackedHuntModeType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.varn.VarnTypeList
import org.runerealm.game.type.varp.VarpTypeList

public class NpcPlayerHuntProcessor
@Inject
constructor(
    @CoreRandom private val random: GameRandom,
    private val mapClock: MapClock,
    private val hunt: Hunt,
    private val huntModes: HuntModeTypeList,
    private val varpTypes: VarpTypeList,
    private val varnTypes: VarnTypeList,
    private val objTypes: ObjTypeList,
) {
    public fun process(npc: Npc) {
        if (!npc.isValidTarget() || npc.isDelayed) {
            return
        }

        val huntMode = npc.huntMode
        val huntDisabled = npc.huntRange == 0 || huntMode == null
        if (huntDisabled) {
            return
        }

        val skipHunt = !npc.isAnyoneNear()
        if (skipHunt) {
            return
        }

        val huntType = huntModes.getValue(huntMode)
        val huntDelayed = npc.huntClock < huntType.rate - 1
        if (huntDelayed) {
            return
        }

        if (huntType.type == HuntType.Player && !npc.hasInteraction()) {
            npc.huntPlayer(huntType)
        }
    }

    private fun Npc.huntPlayer(mode: UnpackedHuntModeType) {
        var target = PlayerUid.NULL
        var count = 0

        val players = hunt.findPlayers(coords, huntRange, mode.checkVis)
        for (player in players) {
            if (player.isInvisible) {
                continue
            }

            if (mode.checkNotBusy && player.isBusy) {
                continue
            }

            if (mode.checkAfk && player.isAfk()) {
                continue
            }

            if (mode.checkNotTooStrong == HuntCheckNotTooStrong.OutsideWilderness) {
                if (player.combatLevel > type.vislevel * 2 && !player.isInWilderness()) {
                    continue
                }
            }

            if (!player.isInMulti()) {
                if (mode.checkNotCombat != -1) {
                    val varp = varpTypes.getValue(mode.checkNotCombat)
                    val delay = player.vars[varp] + constants.combat_activecombat_delay
                    if (delay > mapClock.cycle) {
                        continue
                    }
                }

                if (mode.checkNotCombatSelf != -1) {
                    val varn = varnTypes.getValue(mode.checkNotCombatSelf)
                    val delay = vars[varn] + constants.combat_activecombat_delay
                    if (delay > mapClock.cycle) {
                        continue
                    }
                }
            }

            val checkVar1 = mode.checkVar1
            if (checkVar1 != null) {
                val varp = varpTypes.getValue(checkVar1.varp)
                val actual = player.vars[varp]
                if (!checkVar1.evaluate(actual)) {
                    continue
                }
            }

            val checkVar2 = mode.checkVar2
            if (checkVar2 != null) {
                val varp = varpTypes.getValue(checkVar2.varp)
                val actual = player.vars[varp]
                if (!checkVar2.evaluate(actual)) {
                    continue
                }
            }

            val checkVar3 = mode.checkVar3
            if (checkVar3 != null) {
                val varp = varpTypes.getValue(checkVar3.varp)
                val actual = player.vars[varp]
                if (!checkVar3.evaluate(actual)) {
                    continue
                }
            }

            val checkInvObj = mode.checkInvObj
            if (checkInvObj != null) {
                val inventory = player.invMap.backing[checkInvObj.inv]

                val obj = checkInvObj.type
                val count = inventory?.sumOf { if (it?.id == obj) it.count else 0 } ?: 0

                if (!checkInvObj.evaluate(count)) {
                    continue
                }
            }

            val checkInvParam = mode.checkInvParam
            if (checkInvParam != null) {
                val inventory = player.invMap.backing[checkInvParam.inv]
                val param = checkInvParam.type

                var count = 0
                if (inventory != null) {
                    for (invObj in inventory) {
                        val objType = objTypes.getOrNull(invObj) ?: continue
                        val value = objType.paramMap?.primitiveMap?.get(param) ?: continue
                        if (value !is Int) {
                            val message = "Expected param value to be Int: $value (param=$param)"
                            throw IllegalStateException(message)
                        }
                        count += value
                    }
                }

                if (!checkInvParam.evaluate(count)) {
                    continue
                }
            }

            count++
            if (random.of(minInclusive = 0, maxInclusive = count) == 0) {
                target = player.uid
            }
        }

        if (target != PlayerUid.NULL) {
            huntPlayer = target
        }
    }

    // TODO: Investigate what `checkAfk` actually checks.
    private fun Player.isAfk(): Boolean {
        return false
    }

    private fun Player.isInWilderness(): Boolean = WildernessArea.contains(coords)

    // Hunt can be quite expensive if not careful. We are assuming that using a possibly delayed
    // multiway indicator will not have any inaccuracies in emulation. If it does, we can change
    // this to a dynamic lookup on `AreaIndex` instead.
    private fun Player.isInMulti(): Boolean {
        return vars[varbits.multiway_indicator] == 1
    }
}
