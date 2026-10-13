package org.runerealm.api.cache

import com.google.inject.Provider
import com.google.inject.Provides
import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.runerealm.annotations.GameCache
import org.runerealm.api.cache.types.TypeListMapDecoder
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeListMap
import org.runerealm.game.type.area.AreaTypeList
import org.runerealm.game.type.category.CategoryTypeList
import org.runerealm.game.type.comp.ComponentTypeList
import org.runerealm.game.type.dbrow.DbRowTypeList
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.game.type.font.FontMetricsTypeList
import org.runerealm.game.type.gameval.GameValNameMap
import org.runerealm.game.type.headbar.HeadbarTypeList
import org.runerealm.game.type.hitmark.HitmarkTypeList
import org.runerealm.game.type.hunt.HuntModeTypeList
import org.runerealm.game.type.idk.IdentKitTypeList
import org.runerealm.game.type.interf.InterfaceTypeList
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.jingle.JingleTypeList
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.midi.MidiTypeList
import org.runerealm.game.type.mod.ModLevelTypeList
import org.runerealm.game.type.model.ModelTypeList
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.param.ParamTypeList
import org.runerealm.game.type.proj.ProjAnimTypeList
import org.runerealm.game.type.seq.SeqTypeList
import org.runerealm.game.type.spot.SpotanimTypeList
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.game.type.struct.StructTypeList
import org.runerealm.game.type.synth.SynthTypeList
import org.runerealm.game.type.varbit.VarBitTypeList
import org.runerealm.game.type.varn.VarnTypeList
import org.runerealm.game.type.varnbit.VarnBitTypeList
import org.runerealm.game.type.varp.VarpTypeList
import org.runerealm.game.type.walktrig.WalkTriggerTypeList
import org.runerealm.module.ExtendedModule

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

    @Provides public fun synthTypeList(map: TypeListMap): SynthTypeList = map.synths

    @Provides public fun structTypeList(map: TypeListMap): StructTypeList = map.structs

    @Provides public fun spotanimTypeList(map: TypeListMap): SpotanimTypeList = map.spotanims

    @Provides public fun jingleTypeList(map: TypeListMap): JingleTypeList = map.jingles

    @Provides
    public fun walkTriggerTypeList(map: TypeListMap): WalkTriggerTypeList = map.walkTriggers

    @Provides public fun varnTypeList(map: TypeListMap): VarnTypeList = map.varns

    @Provides public fun varnBitTypeList(map: TypeListMap): VarnBitTypeList = map.varnbits

    @Provides public fun headbarTypeList(map: TypeListMap): HeadbarTypeList = map.headbars

    @Provides public fun identKitTypeList(map: TypeListMap): IdentKitTypeList = map.identKits

    @Provides public fun hitmarkTypeList(map: TypeListMap): HitmarkTypeList = map.hitmarks

    @Provides public fun categoryTypeList(map: TypeListMap): CategoryTypeList = map.categories

    @Provides public fun projAnimTypeList(map: TypeListMap): ProjAnimTypeList = map.projanims

    @Provides public fun midiTypeList(map: TypeListMap): MidiTypeList = map.midis

    @Provides public fun gameValNameMap(map: TypeListMap): GameValNameMap = map.gameVals

    @Provides public fun areaTypeList(map: TypeListMap): AreaTypeList = map.areas

    @Provides public fun dbRowsTypeList(map: TypeListMap): DbRowTypeList = map.dbRows

    @Provides public fun dbTablesTypeList(map: TypeListMap): DbTableTypeList = map.dbTables

    @Provides public fun huntModeTypeList(map: TypeListMap): HuntModeTypeList = map.hunt

    @Provides public fun modLevelTypeList(map: TypeListMap): ModLevelTypeList = map.modLevels

    @Provides public fun modelTypeList(map: TypeListMap): ModelTypeList = map.models
}

private class ConfigTypeListMapProvider
@Inject
constructor(@GameCache private val cache: Cache, private val names: NameMapping) :
    Provider<TypeListMap> {
    override fun get(): TypeListMap = TypeListMapDecoder.from(cache, names)
}
