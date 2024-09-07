package org.rsmod.objtx

public data class TransactionObj(
    public val id: Int,
    public val count: Int = 1,
    public val vars: Int = 0,
) {
    public val hasVars: Boolean
        get() = vars > 0
}
