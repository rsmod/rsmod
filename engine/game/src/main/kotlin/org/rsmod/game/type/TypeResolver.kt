package org.rsmod.game.type

import org.rsmod.game.type.bas.BasType
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.comp.HashedComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.controller.ControllerType
import org.rsmod.game.type.currency.CurrencyType
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.HashedEnumType
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.font.FontMetricsType
import org.rsmod.game.type.font.HashedFontMetricsType
import org.rsmod.game.type.interf.HashedInterfaceType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.inv.HashedInvType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.loc.HashedLocType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.type.mod.ModLevel
import org.rsmod.game.type.npc.HashedNpcType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.HashedObjType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.param.HashedParamType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.HashedSeqType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.HashedSpotanimType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.HashedStatType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.struct.HashedStructType
import org.rsmod.game.type.struct.StructType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.type.varbit.HashedVarBitType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varcon.VarConType
import org.rsmod.game.type.varconbit.VarConBitType
import org.rsmod.game.type.varobjbit.VarObjBitType
import org.rsmod.game.type.varp.HashedVarpType
import org.rsmod.game.type.varp.VarpLifetime
import org.rsmod.game.type.varp.VarpTransmitLevel
import org.rsmod.game.type.varp.VarpType
import org.rsmod.game.type.walktrig.WalkTriggerPriority
import org.rsmod.game.type.walktrig.WalkTriggerType

public object TypeResolver {
    public operator fun get(type: BasType): Int = type.internalId

    public operator fun set(type: BasType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: CurrencyType): Int? = type.internalId

    public operator fun set(type: CurrencyType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: DropTriggerType): Int? = type.internalId

    public operator fun set(type: DropTriggerType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ModGroup): Int = type.internalId

    public operator fun set(type: ModGroup, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ModLevel): Int = type.internalId

    public operator fun set(type: ModLevel, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: CategoryType): Int? = type.internalId

    public operator fun set(type: CategoryType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ContentGroupType): Int? = type.internalId

    public operator fun set(type: ContentGroupType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ControllerType): Int? = type.internalId

    public operator fun set(type: ControllerType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: InterfaceType): Int? = type.internalId

    public operator fun set(type: InterfaceType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: InterfaceType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedInterfaceType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun <K, V> get(type: EnumType<K, V>): Int? = type.internalId

    public operator fun <K : Any, V : Any> set(type: EnumType<K, V>, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: EnumType<*, *>, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedEnumType<*, *>, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: FontMetricsType): Int? = type.internalId

    public operator fun set(type: FontMetricsType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: FontMetricsType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedFontMetricsType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: ComponentType): Int? = type.internalId

    public operator fun set(type: ComponentType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: ComponentType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedComponentType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: InvType): Int? = type.internalId

    public operator fun set(type: InvType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: InvType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedInvType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: JingleType): Int? = type.internalId

    public operator fun set(type: JingleType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: LocType): Int? = type.internalId

    public operator fun set(type: LocType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: LocType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedLocType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: MesAnimType): Int = type.internalId

    public operator fun set(type: MesAnimType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: NpcType): Int? = type.internalId

    public operator fun set(type: NpcType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: NpcType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedNpcType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: ObjType): Int? = type.internalId

    public operator fun set(type: ObjType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: ObjType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedObjType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: QueueType): Int? = type.internalId

    public operator fun set(type: QueueType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: SeqType): Int? = type.internalId

    public operator fun set(type: SeqType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: SeqType, internalName: String) {
        type.internalName = internalName
    }

    public fun setPriority(type: SeqType, priority: Int) {
        type.internalPriority = priority
    }

    public operator fun set(type: HashedSeqType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: SpotanimType): Int? = type.internalId

    public operator fun set(type: SpotanimType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: SpotanimType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedSpotanimType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: StatType): Int? = type.internalId

    public operator fun set(type: StatType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: StatType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedStatType, identityHash: Long) {
        type.startHash = identityHash
    }

    public fun setDisplayName(type: StatType, displayName: String) {
        type.internalDisplayName = displayName
    }

    public fun setMaxLevel(type: StatType, maxLevel: Int) {
        type.internalMaxLevel = maxLevel
    }

    public operator fun get(type: StructType): Int? = type.internalId

    public operator fun set(type: StructType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: StructType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedStructType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: SynthType): Int? = type.internalId

    public operator fun set(type: SynthType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: TimerType): Int? = type.internalId

    public operator fun set(type: TimerType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ParamType<*>): Int? = type.internalId

    public operator fun set(type: ParamType<*>, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: ParamType<*>, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedParamType<*>, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: VarConType): Int? = type.internalId

    public operator fun set(type: VarConType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: VarConBitType): Int? = type.internalId

    public operator fun set(type: VarConBitType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: VarBitType): Int? = type.internalId

    public operator fun set(type: VarBitType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: VarBitType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: HashedVarBitType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: VarpType): Int? = type.internalId

    public operator fun set(type: VarpType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: VarpType, internalName: String) {
        type.internalName = internalName
    }

    public fun setScope(type: VarpType, scope: VarpLifetime) {
        type.internalScope = scope
    }

    public fun setTransmit(type: VarpType, transmit: VarpTransmitLevel) {
        type.internalTransmit = transmit
    }

    public operator fun set(type: HashedVarpType, identityHash: Long) {
        type.startHash = identityHash
    }

    public operator fun get(type: VarObjBitType): Int? = type.internalId

    public operator fun set(type: VarObjBitType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: VarBitType, baseVar: VarpType) {
        type.internalVarp = baseVar
    }

    public operator fun set(type: VarBitType, bitRange: IntRange) {
        type.internalLsb = bitRange.first
        type.internalMsb = bitRange.last
    }

    public fun <K : Any, V : Any> setTypedMap(type: UnpackedEnumType<K, V>, map: Map<K, V?>) {
        type.typedMap = map
    }

    public fun <K : Any, V : Any> setDefault(type: UnpackedEnumType<K, V>, default: V?) {
        type.default = default
    }

    public fun <T : Any> setDefault(type: ParamType<T>, default: T?) {
        type.typedDefault = default
    }

    public operator fun get(type: WalkTriggerType): Int? = type.internalId

    public operator fun set(type: WalkTriggerType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: WalkTriggerType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: WalkTriggerType, priority: WalkTriggerPriority) {
        type.internalPriority = priority
    }
}
