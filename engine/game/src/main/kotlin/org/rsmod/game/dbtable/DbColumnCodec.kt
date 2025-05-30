package org.rsmod.game.dbtable

import kotlin.reflect.KClass
import org.rsmod.game.stat.StatRequirement
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.literal.CacheVarLiteral
import org.rsmod.game.type.literal.CacheVarTypeMap.codecOut
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.stat.StatType
import org.rsmod.map.CoordGrid

public interface DbColumnCodec<T, R> {
    public val types: List<CacheVarLiteral>

    public fun decode(iterator: Iterator<T, R>, types: TypeListMap): R

    public fun encode(value: R): T

    public class Iterator<T, R>(
        private val codec: DbColumnCodec<T, R>,
        private val values: List<T>,
        private val types: TypeListMap,
    ) {
        private var position = 0

        public fun next(): T {
            return values[position++]
        }

        public fun nextInt(): Int {
            return next() as Int
        }

        public fun nextString(): String {
            return next() as String
        }

        public fun hasNext(): Boolean {
            return position < values.size
        }

        public fun single(): R = codec.decode(this, types)

        public fun toList(): List<R> {
            return buildList {
                while (hasNext()) {
                    this += codec.decode(this@Iterator, types)
                }
            }
        }
    }

    public object AreaTypeCodec : BaseIntCodec<AreaType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.AREA)

        override fun decode(iterator: Iterator<Int, AreaType>, types: TypeListMap): AreaType {
            val type = iterator.next()
            return types.areas.getValue(type).toHashedType()
        }

        override fun encode(value: AreaType): Int {
            return value.id
        }
    }

    public interface BaseIntCodec<R> : DbColumnCodec<Int, R>

    public object BooleanCodec : BaseIntCodec<Boolean> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.BOOL)

        override fun decode(iterator: Iterator<Int, Boolean>, types: TypeListMap): Boolean {
            return iterator.next() == 1
        }

        override fun encode(value: Boolean): Int {
            return if (value) 1 else 0
        }
    }

    public object ComponentTypeCodec : BaseIntCodec<ComponentType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.COMPONENT)

        override fun decode(
            iterator: Iterator<Int, ComponentType>,
            types: TypeListMap,
        ): ComponentType {
            val type = iterator.next()
            return types.components.getValue(type).toHashedType()
        }

        override fun encode(value: ComponentType): Int {
            return value.packed
        }
    }

    public object CoordGridCodec : BaseIntCodec<CoordGrid> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.COORDGRID)

        override fun decode(iterator: Iterator<Int, CoordGrid>, types: TypeListMap): CoordGrid {
            val packed = iterator.next()
            return CoordGrid(packed)
        }

        override fun encode(value: CoordGrid): Int {
            return value.packed
        }
    }

    public object DbRowTypeCodec : BaseIntCodec<DbRowType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.DBROW)

        override fun decode(iterator: Iterator<Int, DbRowType>, types: TypeListMap): DbRowType {
            val type = iterator.next()
            return types.dbRows.getValue(type).toHashedType()
        }

        override fun encode(value: DbRowType): Int {
            return value.id
        }
    }

    public class EnumTypeCodec<K : Any, V : Any>(
        private val keyType: KClass<K>,
        private val valType: KClass<V>,
    ) : BaseIntCodec<EnumType<K, V>> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.ENUM)

        override fun decode(
            iterator: Iterator<Int, EnumType<K, V>>,
            types: TypeListMap,
        ): EnumType<K, V> {
            val cacheType = types.enums.getValue(iterator.next())
            val typeKey = cacheType.keyLiteral.codecOut
            val typeVal = cacheType.valLiteral.codecOut
            if (typeKey != keyType || typeVal != valType) {
                val message =
                    "Unexpected enum types: enum='${cacheType.internalName}', " +
                        "expected=<${typeKey.simpleName}, ${typeVal.simpleName}>, " +
                        "actual=<${keyType.simpleName}, ${valType.simpleName}>"
                throw IllegalArgumentException(message)
            }
            @Suppress("UNCHECKED_CAST")
            return cacheType.toHashedType() as EnumType<K, V>
        }

        override fun encode(value: EnumType<K, V>): Int {
            return value.id
        }
    }

    public object IntCodec : BaseIntCodec<Int> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.INT)

        override fun decode(iterator: Iterator<Int, Int>, types: TypeListMap): Int {
            return iterator.next()
        }

        override fun encode(value: Int): Int {
            return value
        }
    }

    public object InterfaceTypeCodec : BaseIntCodec<InterfaceType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.INTERFACE)

        override fun decode(
            iterator: Iterator<Int, InterfaceType>,
            types: TypeListMap,
        ): InterfaceType {
            val type = iterator.next()
            return types.interfaces.getValue(type).toHashedType()
        }

        override fun encode(value: InterfaceType): Int {
            return value.id
        }
    }

    public object LocTypeCodec : BaseIntCodec<LocType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.LOC)

        override fun decode(iterator: Iterator<Int, LocType>, types: TypeListMap): LocType {
            val type = iterator.next()
            return types.locs.getValue(type).toHashedType()
        }

        override fun encode(value: LocType): Int {
            return value.id
        }
    }

    public object MidiTypeCodec : BaseIntCodec<MidiType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.MIDI)

        override fun decode(iterator: Iterator<Int, MidiType>, types: TypeListMap): MidiType {
            val type = iterator.next()
            return types.midis.getValue(type).toHashedType()
        }

        override fun encode(value: MidiType): Int {
            return value.id
        }
    }

    public object NpcTypeCodec : BaseIntCodec<NpcType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.NPC)

        override fun decode(iterator: Iterator<Int, NpcType>, types: TypeListMap): NpcType {
            val type = iterator.next()
            return types.npcs.getValue(type).toHashedType()
        }

        override fun encode(value: NpcType): Int {
            return value.id
        }
    }

    public object ObjTypeCodec : BaseIntCodec<ObjType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.OBJ)

        override fun decode(iterator: Iterator<Int, ObjType>, types: TypeListMap): ObjType {
            val type = iterator.next()
            return types.objs.getValue(type).toHashedType()
        }

        override fun encode(value: ObjType): Int {
            return value.id
        }
    }

    public object StatReqCodec : DbColumnCodec<Any, StatRequirement> {
        override val types: List<CacheVarLiteral> =
            listOf(CacheVarLiteral.STAT, CacheVarLiteral.INT)

        override fun decode(
            iterator: Iterator<Any, StatRequirement>,
            types: TypeListMap,
        ): StatRequirement {
            val stat = iterator.nextInt()
            val req = iterator.nextInt()
            val type = types.stats.getValue(stat).toHashedType()
            return StatRequirement(type, req)
        }

        override fun encode(value: StatRequirement): Any {
            return listOf(value.stat.id, value.level)
        }
    }

    public object StatTypeCodec : BaseIntCodec<StatType> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.STAT)

        override fun decode(iterator: Iterator<Int, StatType>, types: TypeListMap): StatType {
            val type = iterator.next()
            return types.stats.getValue(type).toHashedType()
        }

        override fun encode(value: StatType): Int {
            return value.id
        }
    }

    public object StringCodec : DbColumnCodec<String, String> {
        override val types: List<CacheVarLiteral> = listOf(CacheVarLiteral.STRING)

        override fun decode(iterator: Iterator<String, String>, types: TypeListMap): String {
            return iterator.next()
        }

        override fun encode(value: String): String {
            return value
        }
    }
}
