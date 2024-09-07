package org.rsmod.game.type.literal

import kotlin.reflect.KClass
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.map.CoordGrid

public sealed class CacheVarCodec<K, V : Any>(public val out: KClass<V>) {
    public abstract fun decode(types: TypeListMap, value: K): V?

    public abstract fun encode(value: V): K
}

public abstract class BaseIntVarCodec<V : Any>(out: KClass<V>) : CacheVarCodec<Int, V>(out)

public abstract class BaseStringVarCodec<V : Any>(out: KClass<V>) : CacheVarCodec<String, V>(out)

public object CacheVarIntCodec : BaseIntVarCodec<Int>(Int::class) {
    override fun decode(types: TypeListMap, value: Int): Int = value

    override fun encode(value: Int): Int = value
}

public object CacheVarStringCodec : BaseStringVarCodec<String>(String::class) {
    override fun decode(types: TypeListMap, value: String): String = value

    override fun encode(value: String): String = value
}

public object CacheVarBoolCodec : BaseIntVarCodec<Boolean>(Boolean::class) {
    override fun decode(types: TypeListMap, value: Int): Boolean = value == 1

    override fun encode(value: Boolean): Int = if (value) 1 else 0
}

public object CacheVarCoordGridCodec : BaseIntVarCodec<CoordGrid>(CoordGrid::class) {
    override fun decode(types: TypeListMap, value: Int): CoordGrid = CoordGrid(value)

    override fun encode(value: CoordGrid): Int = value.packed
}

public object CacheVarComponentCodec : BaseIntVarCodec<ComponentType>(ComponentType::class) {
    override fun decode(types: TypeListMap, value: Int): ComponentType? =
        types.components[value]?.toHashedType()

    override fun encode(value: ComponentType): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("ComponentType does not have an internal id: $value.")
}

public object CacheVarNamedObjCodec : BaseIntVarCodec<ObjType>(ObjType::class) {
    override fun decode(types: TypeListMap, value: Int): ObjType? = types.objs[value]

    override fun encode(value: ObjType): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("UnpackedObjType does not have an internal id: $value.")
}

public object CacheVarObjCodec : BaseIntVarCodec<ObjType>(ObjType::class) {
    override fun decode(types: TypeListMap, value: Int): ObjType? =
        types.objs[value]?.toHashedType()

    override fun encode(value: ObjType): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("ObjType does not have an internal id: $value.")
}

public object CacheVarSeqCodec : BaseIntVarCodec<SeqType>(SeqType::class) {
    override fun decode(types: TypeListMap, value: Int): SeqType? =
        types.seqs[value]?.toHashedType()

    override fun encode(value: SeqType): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("SeqType does not have an internal id: $value.")
}

public object CacheVarLocCodec : BaseIntVarCodec<LocType>(LocType::class) {
    override fun decode(types: TypeListMap, value: Int): LocType? =
        types.locs[value]?.toHashedType()

    override fun encode(value: LocType): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("LocType does not have an internal id: $value.")
}

public object CacheVarNpcCodec : BaseIntVarCodec<NpcType>(NpcType::class) {
    override fun decode(types: TypeListMap, value: Int): NpcType? =
        types.npcs[value]?.toHashedType()

    override fun encode(value: NpcType): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("NpcType does not have an internal id: $value.")
}

public object CacheVarEnumCodec : BaseIntVarCodec<EnumType<*, *>>(EnumType::class) {
    override fun decode(types: TypeListMap, value: Int): EnumType<Any, Any>? = types.enums[value]

    override fun encode(value: EnumType<*, *>): Int =
        TypeResolver[value]
            ?: throw IllegalStateException("EnumType does not have an internal id: $value.")
}

public object CacheVarSynthCodec : BaseIntVarCodec<SynthType>(SynthType::class) {
    // TODO: Store synths in TypeListMap, no config decoder necessary, but we do want to set the
    //  internal name for each type so we can have debug names here (i.e., so params that store
    //  synth defaults will show the internal synth name and not just an empty string)
    override fun decode(types: TypeListMap, value: Int): SynthType = SynthType(value, "")

    override fun encode(value: SynthType): Int = value.id
}
