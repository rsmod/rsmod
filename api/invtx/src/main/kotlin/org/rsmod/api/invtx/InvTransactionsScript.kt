package org.rsmod.api.invtx

import jakarta.inject.Inject
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.plugin.scripts.SimplePluginScript

// @see [docs/quirks.md] for details on why this is done.
internal lateinit var cachedInventoryTransactions: InvTransactions

public class InvTransactionsScript @Inject constructor(private val objTypes: ObjTypeList) :
    SimplePluginScript() {
    public lateinit var transactions: InvTransactions

    override fun ScriptContext.startUp() {
        val create = InvTransactions.Companion.from(objTypes)
        transactions = create
        cachedInventoryTransactions = create
    }
}
