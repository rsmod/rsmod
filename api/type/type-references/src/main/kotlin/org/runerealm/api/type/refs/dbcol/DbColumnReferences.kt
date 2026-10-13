package org.runerealm.api.type.refs.dbcol

import kotlin.reflect.KClass
import org.runerealm.api.type.refs.TypeReferences
import org.runerealm.game.dbtable.DbColumnCodec
import org.runerealm.game.dbtable.DbListColumn
import org.runerealm.game.dbtable.DbValueColumn
import org.runerealm.game.stat.StatRequirement
import org.runerealm.game.type.area.AreaType
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.dbrow.DbRowType
import org.runerealm.game.type.enums.EnumType
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.midi.MidiType
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.stat.StatType
import org.runerealm.map.CoordGrid

public abstract class DbColumnReferences :
    TypeReferences<NamedDbColumn, Nothing>(NamedDbColumn::class.java) {
    public fun <T, R> value(internal: String, decoder: DbColumnCodec<T, R>): DbValueColumn<T, R> {
        val column = DbValueColumn(decoder)
        cache += NamedDbColumn(internal, column, decoder.types)
        return column
    }

    public fun <T, R> list(internal: String, decoder: DbColumnCodec<T, R>): DbListColumn<T, R> {
        val column = DbListColumn(decoder)
        cache += NamedDbColumn(internal, column, decoder.types)
        return column
    }

    public fun area(internal: String): DbValueColumn<Int, AreaType> {
        return value(internal, DbColumnCodec.AreaTypeCodec)
    }

    public fun boolean(internal: String): DbValueColumn<Int, Boolean> {
        return value(internal, DbColumnCodec.BooleanCodec)
    }

    public fun component(internal: String): DbValueColumn<Int, ComponentType> {
        return value(internal, DbColumnCodec.ComponentTypeCodec)
    }

    public fun coord(internal: String): DbValueColumn<Int, CoordGrid> {
        return value(internal, DbColumnCodec.CoordGridCodec)
    }

    public fun dbRow(internal: String): DbValueColumn<Int, DbRowType> {
        return value(internal, DbColumnCodec.DbRowTypeCodec)
    }

    public fun <K : Any, V : Any> enum(
        internal: String,
        key: KClass<K>,
        value: KClass<V>,
    ): DbValueColumn<Int, EnumType<K, V>> {
        return value(internal, DbColumnCodec.EnumTypeCodec(key, value))
    }

    public fun int(internal: String): DbValueColumn<Int, Int> {
        return value(internal, DbColumnCodec.IntCodec)
    }

    public fun interf(internal: String): DbValueColumn<Int, InterfaceType> {
        return value(internal, DbColumnCodec.InterfaceTypeCodec)
    }

    public fun loc(internal: String): DbValueColumn<Int, LocType> {
        return value(internal, DbColumnCodec.LocTypeCodec)
    }

    public fun midi(internal: String): DbValueColumn<Int, MidiType> {
        return value(internal, DbColumnCodec.MidiTypeCodec)
    }

    public fun npc(internal: String): DbValueColumn<Int, NpcType> {
        return value(internal, DbColumnCodec.NpcTypeCodec)
    }

    public fun obj(internal: String): DbValueColumn<Int, ObjType> {
        return value(internal, DbColumnCodec.ObjTypeCodec)
    }

    public fun stat(internal: String): DbValueColumn<Int, StatType> {
        return value(internal, DbColumnCodec.StatTypeCodec)
    }

    public fun statReq(internal: String): DbValueColumn<Int, StatRequirement> {
        return value(internal, DbColumnCodec.StatReqCodec)
    }

    public fun string(internal: String): DbValueColumn<String, String> {
        return value(internal, DbColumnCodec.StringCodec)
    }
}
