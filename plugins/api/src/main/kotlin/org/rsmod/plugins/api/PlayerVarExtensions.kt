package org.rsmod.plugins.api

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.net.downstream.VarpLarge
import org.rsmod.plugins.api.net.downstream.VarpSmall
import org.rsmod.plugins.api.util.BitUtils
import org.rsmod.plugins.cache.config.varbit.VarbitType
import org.rsmod.plugins.cache.config.varp.VarpType
import org.rsmod.plugins.types.NamedVarp

public fun Player.getVarp(varp: NamedVarp): Int = vars[varp.id] ?: 0

public fun Player.getVarp(type: VarpType): Int = vars[type.id] ?: 0

public fun Player.setVarp(value: Int, type: VarpType) {
    // TODO: is there a scenario where we'd want to keep
    // track of a varp that doesn't persist?
    if (type.persist) {
        vars[type.id] = value
    }
    if (type.transmit) {
        syncVarp(type.id, value)
    }
}

public fun Player.setVarp(value: Boolean, type: VarpType) {
    setVarp(if (value) 1 else 0, type)
}

public fun Player.toggleVarp(type: VarpType) {
    val state = if (getVarp(type) == 1) 0 else 1
    setVarp(state, type)
}

public fun Player.getVarbit(type: VarbitType): Int {
    val varp = vars[type.varp] ?: 0
    return BitUtils.get(varp, type.lsb..type.msb)
}

public fun Player.setVarbit(value: Int, type: VarbitType, persist: Boolean = true) {
    val modifiedValue = BitUtils.modify(
        value = vars[type.varp] ?: 0,
        bitRange = type.lsb..type.msb,
        rangeValue = value
    )
    if (persist) {
        vars[type.varp] = modifiedValue
    }
    if (type.transmit) {
        syncVarp(type.varp, modifiedValue)
    }
}

public fun Player.setVarbit(value: Boolean, type: VarbitType, persist: Boolean = true) {
    setVarbit(if (value) 1 else 0, type, persist)
}

public fun Player.syncVarp(varp: Int, value: Int) {
    val packet = when (value) {
        in Byte.MIN_VALUE..Byte.MAX_VALUE -> VarpSmall(varp, value)
        else -> VarpLarge(varp, value)
    }
    downstream += packet
}
