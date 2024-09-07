package org.rsmod.objtx

public class TransactionCancellation(public val err: TransactionResult.Err) :
    IllegalStateException()
