package org.rsmod.game.type.literal

import kotlin.reflect.KClass
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.dbtable.DbTableType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.headbar.HeadbarType
import org.rsmod.game.type.hitmark.HitmarkType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.proj.ProjAnimType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
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

public object CacheVarAreaCodec : BaseIntVarCodec<AreaType>(AreaType::class) {
    override fun decode(types: TypeListMap, value: Int): AreaType? =
        types.areas[value]?.toHashedType()

    override fun encode(value: AreaType): Int = value.id
}

public object CacheVarCategoryCodec : BaseIntVarCodec<CategoryType>(CategoryType::class) {
    // Note: Not every referenced category requires a symbol name. If a category is not
    // found in the predefined list, we create a new `CategoryType`.
    // This behavior may change in the future if we decide to enforce that every referenced
    // category (e.g., in enum or param types) must have a defined symbol.
    override fun decode(types: TypeListMap, value: Int): CategoryType =
        types.categories[value] ?: CategoryType(value, "unnamed_category_$value")

    override fun encode(value: CategoryType): Int = value.id
}

public object CacheVarCoordGridCodec : BaseIntVarCodec<CoordGrid>(CoordGrid::class) {
    override fun decode(types: TypeListMap, value: Int): CoordGrid = CoordGrid(value)

    override fun encode(value: CoordGrid): Int = value.packed
}

public object CacheVarComponentCodec : BaseIntVarCodec<ComponentType>(ComponentType::class) {
    override fun decode(types: TypeListMap, value: Int): ComponentType? =
        types.components[value]?.toHashedType()

    override fun encode(value: ComponentType): Int = value.packed
}

public object CacheVarDbRowCodec : BaseIntVarCodec<DbRowType>(DbRowType::class) {
    override fun decode(types: TypeListMap, value: Int): DbRowType? =
        types.dbRows[value]?.toHashedType()

    override fun encode(value: DbRowType): Int = value.id
}

public object CacheVarDbTableCodec : BaseIntVarCodec<DbTableType>(DbTableType::class) {
    override fun decode(types: TypeListMap, value: Int): DbTableType? =
        types.dbTables[value]?.toHashedType()

    override fun encode(value: DbTableType): Int = value.id
}

public object CacheVarHeadbarCodec : BaseIntVarCodec<HeadbarType>(HeadbarType::class) {
    override fun decode(types: TypeListMap, value: Int): HeadbarType? =
        types.headbars[value]?.toHashedType()

    override fun encode(value: HeadbarType): Int = value.id
}

public object CacheVarHitmarkCodec : BaseIntVarCodec<HitmarkType>(HitmarkType::class) {
    override fun decode(types: TypeListMap, value: Int): HitmarkType? =
        types.hitmarks[value]?.toHashedType()

    override fun encode(value: HitmarkType): Int = value.id
}

public object CacheVarNamedObjCodec : BaseIntVarCodec<ObjType>(ObjType::class) {
    override fun decode(types: TypeListMap, value: Int): ObjType? = types.objs[value]

    override fun encode(value: ObjType): Int = value.id
}

public object CacheVarObjCodec : BaseIntVarCodec<ObjType>(ObjType::class) {
    override fun decode(types: TypeListMap, value: Int): ObjType? =
        types.objs[value]?.toHashedType()

    override fun encode(value: ObjType): Int = value.id
}

public object CacheVarSeqCodec : BaseIntVarCodec<SeqType>(SeqType::class) {
    override fun decode(types: TypeListMap, value: Int): SeqType? =
        types.seqs[value]?.toHashedType()

    override fun encode(value: SeqType): Int = value.id
}

public object CacheVarSpotanimCodec : BaseIntVarCodec<SpotanimType>(SpotanimType::class) {
    override fun decode(types: TypeListMap, value: Int): SpotanimType? =
        types.spotanims[value]?.toHashedType()

    override fun encode(value: SpotanimType): Int = value.id
}

public object CacheVarLocCodec : BaseIntVarCodec<LocType>(LocType::class) {
    override fun decode(types: TypeListMap, value: Int): LocType? =
        types.locs[value]?.toHashedType()

    override fun encode(value: LocType): Int = value.id
}

public object CacheVarNpcCodec : BaseIntVarCodec<NpcType>(NpcType::class) {
    override fun decode(types: TypeListMap, value: Int): NpcType? =
        types.npcs[value]?.toHashedType()

    override fun encode(value: NpcType): Int = value.id
}

public object CacheVarEnumCodec : BaseIntVarCodec<EnumType<*, *>>(EnumType::class) {
    override fun decode(types: TypeListMap, value: Int): EnumType<Any, Any>? =
        types.enums[value]?.toHashedType()

    override fun encode(value: EnumType<*, *>): Int = value.id
}

public object CacheVarProjAnimCodec : BaseIntVarCodec<ProjAnimType>(ProjAnimType::class) {
    override fun decode(types: TypeListMap, value: Int): ProjAnimType? =
        types.projanims[value]?.toHashedType()

    override fun encode(value: ProjAnimType): Int = value.id
}

public object CacheVarStatCodec : BaseIntVarCodec<StatType>(StatType::class) {
    override fun decode(types: TypeListMap, value: Int): StatType? =
        types.stats[value]?.toHashedType()

    override fun encode(value: StatType): Int = value.id
}

public object CacheVarSynthCodec : BaseIntVarCodec<SynthType>(SynthType::class) {
    // Note: Not every referenced synth requires a symbol name. If a synth is not found in
    // the predefined list, we create a new `SynthType`.
    // This behavior may change in the future if we decide to enforce that every referenced
    // synth (e.g., in enum or param types) must have a defined symbol.
    override fun decode(types: TypeListMap, value: Int): SynthType =
        types.synths[value] ?: SynthType(value, "unnamed_synth_$value")

    override fun encode(value: SynthType): Int = value.id
}

public object CacheVarVarBitCodec : BaseIntVarCodec<VarBitType>(VarBitType::class) {
    override fun decode(types: TypeListMap, value: Int): VarBitType? = types.varbits[value]

    override fun encode(value: VarBitType): Int = value.id
}

public object CacheVarVarpCodec : BaseIntVarCodec<VarpType>(VarpType::class) {
    override fun decode(types: TypeListMap, value: Int): VarpType? = types.varps[value]

    override fun encode(value: VarpType): Int = value.id
}
