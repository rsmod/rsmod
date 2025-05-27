package org.rsmod.api.type.refs.dbcol

import kotlin.reflect.KClass
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.game.dbtable.DbColumnCodec
import org.rsmod.game.dbtable.DbGroupColumn
import org.rsmod.game.dbtable.DbGroupListColumn
import org.rsmod.game.dbtable.DbSingleColumn
import org.rsmod.game.stat.StatRequirement
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.stat.StatType
import org.rsmod.map.CoordGrid

public abstract class DbColumnReferences :
    TypeReferences<NamedDbColumn, Nothing>(NamedDbColumn::class.java) {
    public fun <T, R> single(internal: String, decoder: DbColumnCodec<T, R>): DbSingleColumn<T, R> {
        val column = DbSingleColumn(decoder)
        cache += NamedDbColumn(internal, column, decoder.types)
        return column
    }

    public fun <T, R> group(internal: String, decoder: DbColumnCodec<T, R>): DbGroupColumn<T, R> {
        val column = DbGroupColumn(decoder)
        cache += NamedDbColumn(internal, column, decoder.types)
        return column
    }

    public fun <T, R> groupList(
        internal: String,
        decoder: DbColumnCodec<T, R>,
    ): DbGroupListColumn<T, R> {
        val column = DbGroupListColumn(decoder)
        cache += NamedDbColumn(internal, column, decoder.types)
        return column
    }

    public fun boolean(internal: String): DbSingleColumn<Int, Boolean> {
        return single(internal, DbColumnCodec.BooleanCodec)
    }

    public fun component(internal: String): DbSingleColumn<Int, ComponentType> {
        return single(internal, DbColumnCodec.ComponentTypeCodec)
    }

    public fun coord(internal: String): DbSingleColumn<Int, CoordGrid> {
        return single(internal, DbColumnCodec.CoordGridCodec)
    }

    public fun dbRow(internal: String): DbSingleColumn<Int, DbRowType> {
        return single(internal, DbColumnCodec.DbRowTypeCodec)
    }

    public fun <K : Any, V : Any> enum(
        internal: String,
        key: KClass<K>,
        value: KClass<V>,
    ): DbSingleColumn<Int, EnumType<K, V>> {
        return single(internal, DbColumnCodec.EnumTypeCodec(key, value))
    }

    public fun int(internal: String): DbSingleColumn<Int, Int> {
        return single(internal, DbColumnCodec.IntCodec)
    }

    public fun interf(internal: String): DbSingleColumn<Int, InterfaceType> {
        return single(internal, DbColumnCodec.InterfaceTypeCodec)
    }

    public fun loc(internal: String): DbSingleColumn<Int, LocType> {
        return single(internal, DbColumnCodec.LocTypeCodec)
    }

    public fun npc(internal: String): DbSingleColumn<Int, NpcType> {
        return single(internal, DbColumnCodec.NpcTypeCodec)
    }

    public fun obj(internal: String): DbSingleColumn<Int, ObjType> {
        return single(internal, DbColumnCodec.ObjTypeCodec)
    }

    public fun stat(internal: String): DbSingleColumn<Int, StatType> {
        return single(internal, DbColumnCodec.StatTypeCodec)
    }

    public fun statReq(internal: String): DbGroupColumn<Any, StatRequirement> {
        return group(internal, DbColumnCodec.StatReqCodec)
    }

    public fun statReqList(internal: String): DbGroupListColumn<Any, StatRequirement> {
        return groupList(internal, DbColumnCodec.StatReqCodec)
    }

    public fun string(internal: String): DbSingleColumn<String, String> {
        return single(internal, DbColumnCodec.StringCodec)
    }
}
