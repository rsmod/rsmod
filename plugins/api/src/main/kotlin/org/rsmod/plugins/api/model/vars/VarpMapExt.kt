package org.rsmod.plugins.api.model.vars

import org.rsmod.game.model.vars.VarpMap
import org.rsmod.game.model.vars.type.VarbitType
import org.rsmod.game.model.vars.type.VarpType
import org.rsmod.plugins.api.util.extractBitValue
import org.rsmod.plugins.api.util.withBitValue

fun VarpMap.getVarp(type: VarpType): Int {
    return this[type.id] ?: 0
}

fun VarpMap.setVarp(type: VarpType, value: Int) {
    this[type.id] = value
}

fun VarpMap.setVarp(type: VarpType, flag: Boolean, falseValue: Int = 0, trueValue: Int = 1) {
    setVarp(type, if (flag) trueValue else falseValue)
}

fun VarpMap.toggleVarp(type: VarpType, value1: Int = 0, value2: Int = 1) {
    val newValue = if (getVarp(type) == value1) value2 else value1
    setVarp(type, newValue)
}

fun VarpMap.getVarbit(type: VarbitType): Int {
    val varpValue = this[type.varp] ?: 0
    return varpValue.extractBitValue(type.lsb, type.msb)
}

fun VarpMap.setVarbit(type: VarbitType, value: Int) {
    val curValue = this[type.varp] ?: 0
    val newValue = curValue.withBitValue(type.lsb, type.msb, value)
    this[type.varp] = newValue
}

fun VarpMap.setVarbit(type: VarbitType, flag: Boolean, falseValue: Int = 0, trueValue: Int = 1) {
    setVarbit(type, if (flag) trueValue else falseValue)
}

fun VarpMap.toggleVarbit(type: VarbitType, value1: Int = 0, value2: Int = 1) {
    val newValue = if (getVarbit(type) == value1) value2 else value1
    setVarbit(type, newValue)
}
