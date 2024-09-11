package org.rsmod.api.cache

import com.google.inject.Provider
import com.google.inject.Provides
import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.rsmod.annotations.GameCache
import org.rsmod.api.cache.types.TypeListMapDecoder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.module.ExtendedModule

public object CacheModule : ExtendedModule() {
    override fun bind() {
        bindProvider(ConfigTypeListMapProvider::class.java)
    }

    @Provides public fun locTypeList(map: TypeListMap): LocTypeList = map.locs

    @Provides public fun objTypeList(map: TypeListMap): ObjTypeList = map.objs

    @Provides public fun paramTypeList(map: TypeListMap): ParamTypeList = map.params

    @Provides public fun npcTypeList(map: TypeListMap): NpcTypeList = map.npcs

    @Provides public fun enumTypeList(map: TypeListMap): EnumTypeList = map.enums

    @Provides public fun componentTypeList(map: TypeListMap): ComponentTypeList = map.components

    @Provides public fun interfaceTypeList(map: TypeListMap): InterfaceTypeList = map.interfaces

    @Provides public fun varpTypeList(map: TypeListMap): VarpTypeList = map.varps

    @Provides public fun varBitTypeList(map: TypeListMap): VarBitTypeList = map.varbits

    @Provides public fun invTypeList(map: TypeListMap): InvTypeList = map.invs

    @Provides public fun seqTypeList(map: TypeListMap): SeqTypeList = map.seqs

    @Provides public fun fontMetricTypeList(map: TypeListMap): FontMetricsTypeList = map.fonts

    @Provides public fun statTypeList(map: TypeListMap): StatTypeList = map.stats
}

private class ConfigTypeListMapProvider
@Inject
constructor(@GameCache private val cache: Cache, private val names: NameMapping) :
    Provider<TypeListMap> {
    override fun get(): TypeListMap = TypeListMapDecoder.ofParallel(cache, names)
}
