package org.rsmod.game.type

import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.font.FontMetricsType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.type.mod.ModLevel
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varobjbit.VarObjBitType
import org.rsmod.game.type.varp.VarpType

public object TypeResolver {
    public operator fun get(type: ModGroup): Int? = type.internalId

    public operator fun set(type: ModGroup, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ModLevel): Int? = type.internalId

    public operator fun set(type: ModLevel, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: CategoryType): Int? = type.internalId

    public operator fun set(type: CategoryType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ContentType): Int? = type.internalId

    public operator fun set(type: ContentType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: InterfaceType): Int? = type.internalId

    public operator fun set(type: InterfaceType, id: Int) {
        type.internalId = id
    }

    public operator fun <K, V> get(type: EnumType<K, V>): Int? = type.internalId

    public operator fun <K : Any, V : Any> set(type: EnumType<K, V>, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: FontMetricsType): Int? = type.internalId

    public operator fun set(type: FontMetricsType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: FontMetricsType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun get(type: ComponentType): Int? = type.internalId

    public operator fun set(type: ComponentType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: InvType): Int? = type.internalId

    public operator fun set(type: InvType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: LocType): Int? = type.internalId

    public operator fun set(type: LocType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: MesAnimType): Int? = type.internalId

    public operator fun set(type: MesAnimType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: NpcType): Int? = type.internalId

    public operator fun set(type: NpcType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ObjType): Int? = type.internalId

    public operator fun set(type: ObjType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: SeqType): Int? = type.internalId

    public operator fun set(type: SeqType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: SeqType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun get(type: SynthType): Int? = type.internalId

    public operator fun set(type: SynthType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: ParamType<*>): Int? = type.internalId

    public operator fun set(type: ParamType<*>, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: VarBitType): Int? = type.internalId

    public operator fun set(type: VarBitType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: VarpType): Int? = type.internalId

    public operator fun set(type: VarpType, id: Int) {
        type.internalId = id
    }

    public operator fun get(type: VarObjBitType): Int? = type.internalId

    public operator fun set(type: VarObjBitType, id: Int) {
        type.internalId = id
    }

    public operator fun set(type: InterfaceType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: InvType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: ComponentType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: EnumType<*, *>, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: LocType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: NpcType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: ObjType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: ParamType<*>, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: VarpType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: VarpType, transmit: Boolean) {
        type.transmit = transmit
    }

    public operator fun set(type: VarBitType, internalName: String) {
        type.internalName = internalName
    }

    public operator fun set(type: VarBitType, baseVar: VarpType) {
        type.varp = baseVar
    }

    public operator fun set(type: VarBitType, bitRange: IntRange) {
        type.lsb = bitRange.first
        type.msb = bitRange.last
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
}