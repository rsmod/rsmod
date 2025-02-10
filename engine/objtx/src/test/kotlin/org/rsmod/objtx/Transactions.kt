package org.rsmod.objtx

const val red_partyhat = 1038
const val cert_red_partyhat = 1039
const val placeholder_red_partyhat = 14387
const val abyssal_whip = 4151
const val cert_abyssal_whip = 4152
const val placeholder_abyssal_whip = 14032
const val purple_sweets = 10476
const val placeholder_purple_sweets = 16148
const val pickaxe_handle = 466
const val cert_pickaxe_handle = 467
const val trident_of_the_seas = 11907
const val placeholder_trident_of_the_seas = 14013
const val bronze_arrow = 882
const val placeholder_bronze_arrow = 14572
const val iron_arrow = 884
const val placeholder_iron_arrow = 14573
const val template_for_cert = 799
const val template_for_placeholder = 14401

private val certLookup =
    hashMapOf(
        red_partyhat to TransactionObjTemplate(cert_red_partyhat, 0),
        cert_red_partyhat to TransactionObjTemplate(red_partyhat, 799),
        abyssal_whip to TransactionObjTemplate(cert_abyssal_whip, 0),
        cert_abyssal_whip to TransactionObjTemplate(abyssal_whip, 799),
        pickaxe_handle to TransactionObjTemplate(cert_pickaxe_handle, 0),
        cert_pickaxe_handle to TransactionObjTemplate(pickaxe_handle, 799),
    )

private val placeholderLookup =
    hashMapOf(
        red_partyhat to TransactionObjTemplate(placeholder_red_partyhat, 0),
        placeholder_red_partyhat to TransactionObjTemplate(red_partyhat, 14401),
        abyssal_whip to TransactionObjTemplate(placeholder_abyssal_whip, 0),
        placeholder_abyssal_whip to TransactionObjTemplate(abyssal_whip, 14401),
        purple_sweets to TransactionObjTemplate(placeholder_purple_sweets, 0),
        placeholder_purple_sweets to TransactionObjTemplate(purple_sweets, 14401),
        trident_of_the_seas to TransactionObjTemplate(placeholder_trident_of_the_seas, 0),
        placeholder_trident_of_the_seas to TransactionObjTemplate(trident_of_the_seas, 14401),
        bronze_arrow to TransactionObjTemplate(placeholder_bronze_arrow, 0),
        placeholder_bronze_arrow to TransactionObjTemplate(bronze_arrow, 14401),
        iron_arrow to TransactionObjTemplate(placeholder_iron_arrow, 0),
        placeholder_iron_arrow to TransactionObjTemplate(iron_arrow, 14401),
    )

private val stacksLookup = hashSetOf(purple_sweets, bronze_arrow, iron_arrow)

private val dummyitemLookup = hashSetOf(template_for_cert, template_for_placeholder)

fun transaction(init: Transaction<Obj>.() -> Unit): TransactionResultList<Obj> {
    val transaction = Transaction(input = Obj?::toTransactionObj, output = TransactionObj?::toObj)
    transaction.certLookup = certLookup
    transaction.placeholderLookup = placeholderLookup
    transaction.stackableLookup = stacksLookup
    transaction.dummyitemLookup = dummyitemLookup
    try {
        transaction.apply(init)
    } catch (_: TransactionCancellation) {}
    val results = transaction.results()
    if (results.success && transaction.autoCommit) {
        results.commitAll()
    }
    return results
}

fun Transaction<Obj>.select(inv: Inventory): TransactionInventory<Obj> {
    val image = Array(inv.objs.size) { input(inv.objs[it]) }
    val transformed = TransactionInventory(inv.stackType, inv.objs, image, inv.placeholders)
    register(transformed)
    return transformed
}

fun inv(): Inventory = Inventory(TransactionInventory.NormalStack, arrayOfNulls(28))

fun worn(): Inventory = Inventory(TransactionInventory.NormalStack, arrayOfNulls(15))

fun bank(): Inventory =
    Inventory(TransactionInventory.AlwaysStack, arrayOfNulls(1220), placeholders = true)

private fun Obj?.toTransactionObj(): TransactionObj? =
    if (this != null) {
        TransactionObj(id, count, vars)
    } else {
        null
    }

private fun TransactionObj?.toObj(): Obj? =
    if (this != null) {
        Obj(id, count, vars)
    } else {
        null
    }
