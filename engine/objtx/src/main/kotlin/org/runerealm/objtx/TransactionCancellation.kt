package org.runerealm.objtx

public class TransactionCancellation(public val err: TransactionResult.Err) :
    IllegalStateException()
