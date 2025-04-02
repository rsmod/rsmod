package org.rsmod.game.entity.npc

@JvmInline
public value class OpVisibility(public val packed: Int) {
    public companion object {
        private val Boolean.asInt: Int
            get() = if (this) 1 else 0

        private fun pack(
            op1: Boolean,
            op2: Boolean,
            op3: Boolean,
            op4: Boolean,
            op5: Boolean,
        ): Int =
            op1.asInt or
                (op2.asInt shl 1) or
                (op3.asInt shl 2) or
                (op4.asInt shl 3) or
                (op5.asInt shl 4)

        public fun showAll(): OpVisibility =
            OpVisibility(pack(op1 = true, op2 = true, op3 = true, op4 = true, op5 = true))

        public fun hideAll(): OpVisibility =
            OpVisibility(pack(op1 = false, op2 = false, op3 = false, op4 = false, op5 = false))
    }
}
