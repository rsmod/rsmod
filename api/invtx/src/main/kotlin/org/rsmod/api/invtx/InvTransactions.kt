package org.rsmod.api.invtx

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.util.UncheckedType
import org.rsmod.objtx.Transaction
import org.rsmod.objtx.TransactionCancellation
import org.rsmod.objtx.TransactionObj
import org.rsmod.objtx.TransactionObjTemplate
import org.rsmod.objtx.TransactionResultList

public class InvTransactions(
    public val certLookup: Map<Int, TransactionObjTemplate>,
    public val transformLookup: Map<Int, TransactionObjTemplate>,
    public val placeholderLookup: Map<Int, TransactionObjTemplate>,
    public val stackableLookup: Set<Int>,
) {
    public fun transaction(
        autoCommit: Boolean,
        init: Transaction<InvObj>.() -> Unit,
    ): TransactionResultList<InvObj> {
        contract { callsInPlace(init, InvocationKind.AT_MOST_ONCE) }
        val transaction =
            Transaction(input = InvObj?::toTransactionObj, output = TransactionObj?::toObj)
        transaction.autoCommit = autoCommit
        transaction.certLookup = certLookup
        transaction.transformLookup = transformLookup
        transaction.placeholderLookup = placeholderLookup
        transaction.stackableLookup = stackableLookup
        try {
            transaction.apply(init)
        } catch (_: TransactionCancellation) {
            /* cancellation is normal */
        }
        val results = transaction.results()
        if (results.success && transaction.autoCommit) {
            results.commitAll()
        }
        return results
    }

    public companion object {
        public fun from(types: ObjTypeList): InvTransactions {
            val certLookup = types.values.toCertLookup()
            val transformLookup = types.values.toTransformLookup()
            val placeholderLookup = types.values.toPlaceholderLookup()
            val stackableLookup = types.values.filter { it.stackable }.map { it.id }
            return InvTransactions(
                certLookup = Int2ObjectOpenHashMap(certLookup),
                transformLookup = Int2ObjectOpenHashMap(transformLookup),
                placeholderLookup = Int2ObjectOpenHashMap(placeholderLookup),
                stackableLookup = IntOpenHashSet(stackableLookup),
            )
        }

        private fun Iterable<UnpackedObjType>.toCertLookup(): Map<Int, TransactionObjTemplate> =
            filter { it.certlink != 0 }
                .associate { it.id to TransactionObjTemplate(it.certlink, it.certtemplate) }

        private fun Iterable<UnpackedObjType>.toTransformLookup():
            Map<Int, TransactionObjTemplate> =
            filter { it.transformlink != 0 }
                .associate {
                    it.id to TransactionObjTemplate(it.transformlink, it.transformtemplate)
                }

        private fun Iterable<UnpackedObjType>.toPlaceholderLookup():
            Map<Int, TransactionObjTemplate> =
            filter { it.placeholderlink != 0 }
                .associate {
                    it.id to TransactionObjTemplate(it.placeholderlink, it.placeholdertemplate)
                }
    }
}

private fun InvObj?.toTransactionObj(): TransactionObj? =
    if (this != null) {
        TransactionObj(id, count, vars)
    } else {
        null
    }

@OptIn(UncheckedType::class)
private fun TransactionObj?.toObj(): InvObj? =
    if (this != null) {
        InvObj(id, count, vars)
    } else {
        null
    }
