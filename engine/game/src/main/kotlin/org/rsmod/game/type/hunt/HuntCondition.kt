package org.rsmod.game.type.hunt

public sealed class HuntCondition {
    public data class InvCondition(
        val inv: Int,
        val type: Int,
        val operator: Operator,
        val required: Int,
    ) : HuntCondition() {
        public fun evaluate(value: Int): Boolean = operator.evaluate(value, required)
    }

    public data class LocCondition(val loc: Int?, val category: Int?) : HuntCondition()

    public data class NpcCondition(val npc: Int?, val category: Int?) : HuntCondition()

    public data class ObjCondition(val obj: Int?, val category: Int?) : HuntCondition()

    public data class VarCondition(val varp: Int, val operator: Operator, val required: Int) :
        HuntCondition() {
        public fun evaluate(value: Int): Boolean = operator.evaluate(value, required)
    }

    public enum class Operator(public val id: Int) {
        GreaterThan(0),
        LessThan(1),
        Equals(2),
        NotEquals(3);

        public fun evaluate(actual: Int, required: Int): Boolean {
            return when (this) {
                GreaterThan -> actual > required
                LessThan -> actual < required
                Equals -> actual == required
                NotEquals -> actual != required
            }
        }

        public companion object {
            public operator fun get(id: Int): Operator? =
                when (id) {
                    GreaterThan.id -> GreaterThan
                    LessThan.id -> LessThan
                    Equals.id -> Equals
                    NotEquals.id -> NotEquals
                    else -> null
                }
        }
    }
}
