package org.rsmod.api.player.worn

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.api.invtx.invTransaction
import org.rsmod.api.invtx.select
import org.rsmod.api.invtx.swap
import org.rsmod.api.invtx.transfer
import org.rsmod.api.player.events.interact.HeldEquipEvents
import org.rsmod.api.player.righthand
import org.rsmod.api.utils.format.addArticle
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.stat.StatType
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.isErr

public class HeldEquipOp
@Inject
constructor(private val objTypes: ObjTypeList, private val eventBus: EventBus) {
    public fun equip(player: Player, invSlot: Int, inventory: Inventory): HeldEquipResult {
        val obj = inventory[invSlot] ?: return HeldEquipResult.Fail.InvalidObj
        val objType = objTypes[obj]

        val result = equip(player, objType)

        if (result is HeldEquipResult.Success) {
            val (unequipWearpos, primaryWearpos) = result
            val into = player.worn

            // Cache objs to publish as events after successful transaction.
            val unequipObjs =
                (unequipWearpos + primaryWearpos).associateWith {
                    into[it.slot]?.let(objTypes::get)
                }
            val unequipPrimary = into[primaryWearpos.slot] != null

            val transaction =
                player.invTransaction(inventory, into) {
                    val inv = select(inventory)
                    val worn = select(into)
                    swap(
                        from = inv,
                        fromSlot = invSlot,
                        intoSlot = primaryWearpos.slot,
                        into = worn,
                        mergeStacks = true,
                    )
                    for (unequip in unequipWearpos) {
                        val wornObj = into[unequip.slot] ?: continue
                        transfer(
                            from = worn,
                            fromSlot = unequip.slot,
                            count = wornObj.count,
                            into = inv,
                            intoSlot = if (unequipPrimary) null else invSlot,
                        )
                    }
                }

            val equipTransaction = transaction[0]
            if (equipTransaction.isErr()) {
                check(equipTransaction is TransactionResult.NotEnoughSpace) {
                    "Transaction error is expected to only be of " +
                        "`NotEnoughSpace` type: found=$equipTransaction"
                }
                val message = "You don't have enough free space to do that."
                return HeldEquipResult.Fail.NotEnoughWornSpace(message)
            }

            val unequipTransactionErr = transaction.err
            if (unequipTransactionErr != null) {
                check(unequipTransactionErr is TransactionResult.NotEnoughSpace) {
                    "Transaction error is expected to only be of " +
                        "`NotEnoughSpace` type: found=$unequipTransactionErr"
                }
                val message = "You don't have enough free inventory space to do that."
                return HeldEquipResult.Fail.NotEnoughInvSpace(message)
            }

            val change = HeldEquipEvents.WearposChange(player, objType, unequipObjs.values)
            eventBus.publish(change)

            for ((wearpos, type) in unequipObjs) {
                val unequipType = type ?: continue
                val unequip = HeldEquipEvents.Unequip(player, wearpos, unequipType)
                eventBus.publish(unequip)
            }

            val equip = HeldEquipEvents.Equip(player, invSlot, primaryWearpos, objType)
            eventBus.publish(equip)

            player.rebuildAppearance()
        }

        return result
    }

    private fun equip(player: Player, type: UnpackedObjType): HeldEquipResult {
        val statRequirements =
            type.statRequirements().filter { player.statMap.getBaseLevel(it.stat) < it.level }
        if (statRequirements.isNotEmpty()) {
            val messages = type.toMessages(statRequirements)
            return HeldEquipResult.Fail.StatRequirements(messages)
        }

        val wearpos1 = Wearpos[type.wearpos1] ?: return HeldEquipResult.Fail.InvalidObj
        val wearpos2 = Wearpos[type.wearpos2]?.takeUnless { it.isClientOnly }
        val wearpos3 = Wearpos[type.wearpos3]?.takeUnless { it.isClientOnly }

        val unequipWearpos = mutableListOf<Wearpos>()
        wearpos2?.let(unequipWearpos::add)
        wearpos3?.let(unequipWearpos::add)
        if (wearpos1 == Wearpos.LeftHand) {
            val twoHanded = player.righthand?.takeIf { objTypes[it].isTwoHanded() }
            if (twoHanded != null && Wearpos.RightHand !in unequipWearpos) {
                unequipWearpos += Wearpos.RightHand
            }
        }

        return HeldEquipResult.Success(unequipWearpos, wearpos1)
    }

    private fun UnpackedObjType.statRequirements(): List<StatRequirement> {
        val skillReq1 = paramOrNull(params.statreq1_skill)
        val skillReq2 = paramOrNull(params.statreq2_skill)
        if (skillReq1 == null && skillReq2 == null) {
            return emptyList()
        }
        val levelReq1 = paramOrNull(params.statreq1_level) ?: 0
        val levelReq2 = paramOrNull(params.statreq2_level) ?: 0
        val statReq1 = skillReq1?.let { StatRequirement(it, levelReq1) }
        val statReq2 = skillReq2?.let { StatRequirement(it, levelReq2) }
        return listOfNotNull(statReq1, statReq2)
    }

    private fun UnpackedObjType.toMessages(reqs: List<StatRequirement>): Pair<String, String> {
        val message1 = param(params.statreq_failmessage1)
        val message2 = paramOrNull(params.statreq_failmessage2)
        val replace =
            when (reqs.size) {
                1 -> {
                    val message = message2 ?: DEFAULT_STAT_MESSAGE1
                    message
                        .replace("{skill1}", reqs[0].stat.displayName.addArticle())
                        .replace("{level1}", reqs[0].level.toString())
                }
                2 -> {
                    val message = message2 ?: DEFAULT_STAT_MESSAGE2
                    message
                        .replace("{skill1}", reqs[0].stat.displayName.addArticle())
                        .replace("{level1}", reqs[0].level.toString())
                        .replace("{skill2}", reqs[1].stat.displayName.addArticle())
                        .replace("{level2}", reqs[1].level.toString())
                }
                else -> error("Obj unexpected stat requirement list size: reqs=$reqs, type=$this")
            }
        return message1 to replace
    }

    private data class StatRequirement(val stat: StatType, val level: Int)

    private fun UnpackedObjType.isTwoHanded(): Boolean =
        wearpos2 == Wearpos.LeftHand.slot || wearpos3 == Wearpos.LeftHand.slot

    private companion object {
        private const val DEFAULT_STAT_MESSAGE1 = "You need to have {skill1} level of {level1}."

        private const val DEFAULT_STAT_MESSAGE2 =
            "You need to have {skill1} level of {level1} and {skill2} level of {level2}."
    }
}
