package org.runerealm.content.interfaces.bank.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ironman.ULTIMATE_IRONMAN_BANK_MESSAGE
import org.runerealm.api.player.ironman.isUltimateIronman
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLoc2
import org.runerealm.api.script.onOpLoc3
import org.runerealm.api.script.onOpLoc4
import org.runerealm.api.script.onOpLoc5
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Bank booths, bank chests, deposit boxes and similar scenery across the game, found by their names
 * and options: "Bank" (and "Use" on bank chests) opens the bank, "Collect" the collection box, and
 * "Deposit"/"Deposit-box" the deposit box.
 */
class BankLocsScript
@Inject
constructor(private val locTypes: LocTypeList, private val depositBox: DepositBoxScript) :
    PluginScript() {
    override fun ScriptContext.startup() {
        for (type in locTypes.values) {
            if (!type.isBankScenery()) {
                continue
            }
            for ((index, op) in type.op.withIndex()) {
                val action = action(type, op) ?: continue
                register(type, index + 1, action)
            }
        }
    }

    private fun UnpackedLocType.isBankScenery(): Boolean {
        val lower = name.lowercase()
        return "bank" in lower || "deposit box" in lower || "deposit chest" in lower
    }

    private fun action(type: UnpackedLocType, op: String?): (suspend ProtectedAccess.() -> Unit)? {
        val lower = type.name.lowercase()
        return when (op) {
            "Bank" -> ({ openBank() })
            "Use" -> if ("bank chest" in lower) ({ openBank() }) else null
            "Collect" -> ({ ifOpenMainModal(interfaces.ge_collection_box) })
            "Deposit",
            "Deposit-box" -> ({ openDepositBox() })
            else -> null
        }
    }

    private fun ScriptContext.register(
        type: UnpackedLocType,
        op: Int,
        action: suspend ProtectedAccess.() -> Unit,
    ) {
        when (op) {
            1 -> onOpLoc1(type) { action() }
            2 -> onOpLoc2(type) { action() }
            3 -> onOpLoc3(type) { action() }
            4 -> onOpLoc4(type) { action() }
            5 -> onOpLoc5(type) { action() }
        }
    }

    private fun ProtectedAccess.openBank() {
        if (player.isUltimateIronman) {
            mes(ULTIMATE_IRONMAN_BANK_MESSAGE)
            return
        }
        ifOpenMainSidePair(main = interfaces.bank_main, side = interfaces.bank_side)
    }

    private fun ProtectedAccess.openDepositBox() {
        if (player.isUltimateIronman) {
            mes(ULTIMATE_IRONMAN_BANK_MESSAGE)
            return
        }
        depositBox.open(this)
    }
}
