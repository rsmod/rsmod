package org.rsmod.game.type.util

@JvmInline
public value class ObjWeight(public val grams: Int) {
    public companion object {
        public fun grams(value: Int): ObjWeight = ObjWeight(value)

        public fun kg(value: Int): ObjWeight = ObjWeight(value * 1000)

        public fun oz(value: Int): ObjWeight = ObjWeight((value * 28.3495).toInt())

        public fun lb(value: Int): ObjWeight = ObjWeight((value * 453.592).toInt())
    }
}
