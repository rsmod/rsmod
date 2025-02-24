package org.rsmod.game.type

import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpLifetime
import org.rsmod.game.type.varp.VarpTransmitLevel
import org.rsmod.game.type.varp.VarpType
import org.rsmod.game.type.walktrig.WalkTriggerPriority
import org.rsmod.game.type.walktrig.WalkTriggerType

public object TypeResolver {
    public operator fun get(type: CacheType): Int? = type.internalId

    public operator fun set(type: CacheType, internalId: Int) {
        type.internalId = internalId
    }

    public operator fun set(type: CacheType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedCacheType, identityHash: Long) {
        type.startHash = identityHash
    }

    public fun <K : Any, V : Any> setTypedMap(type: UnpackedEnumType<K, V>, map: Map<K, V?>) {
        type.typedMap = map
    }

    public fun <K : Any, V : Any> setDefault(type: UnpackedEnumType<K, V>, default: V?) {
        type.default = default
    }

    public fun setPriority(type: SeqType, priority: Int) {
        type.internalPriority = priority
    }

    public fun setDisplayName(type: StatType, displayName: String) {
        type.internalDisplayName = displayName
    }

    public fun setMaxLevel(type: StatType, maxLevel: Int) {
        type.internalMaxLevel = maxLevel
    }

    public fun <T : Any> setDefault(type: ParamType<T>, default: T?) {
        type.typedDefault = default
    }

    public operator fun set(type: VarBitType, baseVar: VarpType) {
        type.internalVarp = baseVar
    }

    public operator fun set(type: VarBitType, bitRange: IntRange) {
        type.internalLsb = bitRange.first
        type.internalMsb = bitRange.last
    }

    public fun setScope(type: VarpType, scope: VarpLifetime) {
        type.internalScope = scope
    }

    public fun setTransmit(type: VarpType, transmit: VarpTransmitLevel) {
        type.internalTransmit = transmit
    }

    public operator fun set(type: WalkTriggerType, priority: WalkTriggerPriority) {
        type.internalPriority = priority
    }
}
