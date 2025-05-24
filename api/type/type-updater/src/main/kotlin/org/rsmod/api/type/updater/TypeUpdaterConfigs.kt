package org.rsmod.api.type.updater

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import jakarta.inject.Inject
import java.nio.file.Path
import org.openrs2.cache.Cache
import org.rsmod.annotations.EnrichedCache
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.api.cache.types.TypeListMapDecoder
import org.rsmod.api.cache.types.area.AreaTypeEncoder
import org.rsmod.api.cache.types.dbrow.DbRowTypeEncoder
import org.rsmod.api.cache.types.dbtable.DbTableTypeEncoder
import org.rsmod.api.cache.types.enums.EnumTypeEncoder
import org.rsmod.api.cache.types.headbar.HeadbarTypeEncoder
import org.rsmod.api.cache.types.hitmark.HitmarkTypeEncoder
import org.rsmod.api.cache.types.hunt.HuntModeTypeEncoder
import org.rsmod.api.cache.types.inv.InvTypeEncoder
import org.rsmod.api.cache.types.loc.LocTypeEncoder
import org.rsmod.api.cache.types.npc.NpcTypeEncoder
import org.rsmod.api.cache.types.obj.ObjTypeEncoder
import org.rsmod.api.cache.types.param.ParamTypeEncoder
import org.rsmod.api.cache.types.proj.ProjAnimTypeEncoder
import org.rsmod.api.cache.types.stat.StatTypeEncoder
import org.rsmod.api.cache.types.varbit.VarBitTypeEncoder
import org.rsmod.api.cache.types.varn.VarnTypeEncoder
import org.rsmod.api.cache.types.varnbit.VarnBitTypeEncoder
import org.rsmod.api.cache.types.varp.VarpTypeEncoder
import org.rsmod.api.cache.types.walktrig.WalkTriggerTypeEncoder
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.area.AreaTypeBuilder
import org.rsmod.game.type.area.UnpackedAreaType
import org.rsmod.game.type.dbrow.DbRowTypeBuilder
import org.rsmod.game.type.dbrow.UnpackedDbRowType
import org.rsmod.game.type.dbtable.DbTableTypeBuilder
import org.rsmod.game.type.dbtable.DbTableTypeList
import org.rsmod.game.type.dbtable.UnpackedDbTableType
import org.rsmod.game.type.enums.EnumTypeBuilder
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.UnpackedHeadbarType
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.UnpackedHitmarkType
import org.rsmod.game.type.hunt.HuntModeTypeBuilder
import org.rsmod.game.type.hunt.UnpackedHuntModeType
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamTypeBuilder
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.param.UnpackedParamType
import org.rsmod.game.type.proj.ProjAnimTypeBuilder
import org.rsmod.game.type.proj.UnpackedProjAnimType
import org.rsmod.game.type.stat.StatTypeBuilder
import org.rsmod.game.type.stat.UnpackedStatType
import org.rsmod.game.type.util.MergeableCacheBuilder
import org.rsmod.game.type.varbit.UnpackedVarBitType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varbit.VarBitTypeBuilder
import org.rsmod.game.type.varn.UnpackedVarnType
import org.rsmod.game.type.varn.VarnTypeBuilder
import org.rsmod.game.type.varnbit.UnpackedVarnBitType
import org.rsmod.game.type.varnbit.VarnBitTypeBuilder
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpTransmitLevel
import org.rsmod.game.type.varp.VarpTypeBuilder
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder

public class TypeUpdaterConfigs
@Inject
constructor(
    @Js5Cache private val js5CachePath: Path,
    @GameCache private val gameCachePath: Path,
    @EnrichedCache private val enrichedCache: Cache,
    private val names: NameMapping,
    private val builders: TypeBuilderResolverMap,
    private val editors: TypeEditorResolverMap,
) {
    public fun updateAll() {
        encodeAll()
    }

    private fun encodeAll() {
        val configs = TypeListMapDecoder.from(enrichedCache, names)
        val updates = collectUpdateMap(configs)
        val params = transmitParamKeys(configs.params, updates.params)
        val varps = transmitVarpKeys(configs.varps, updates.varps)
        val tables = transmitTableKeys(configs.dbTables, updates.dbTables)
        encodeCacheTypes(updates, gameCachePath, EncoderContext.server(params, varps, tables))
        encodeCacheTypes(updates, js5CachePath, EncoderContext.client(params, varps, tables))
    }

    /**
     * Combines and returns a set of all unique [UnpackedParamType.id] values where the
     * [UnpackedParamType.transmit] flag is `true`.
     *
     * This ensures only relevant parameters are included for client cache packing, filtering out
     * server-side parameters.
     *
     * Discrepancies between the existing [cache] and [updates] are resolved by:
     * - Removing outdated `transmit` flags from [cache] that are overridden by [updates].
     * - Including only updated or valid `transmit` keys from [updates].
     *
     * This guarantees that parameters marked with `transmit` in [cache] but later updated to
     * `false` in [updates] are excluded, avoiding incorrect inclusion in the `transmit` keys.
     */
    private fun transmitParamKeys(
        cache: ParamTypeList,
        updates: List<UnpackedParamType<*>>,
    ): Set<Int> {
        val updateParamKeys = IntOpenHashSet(updates.map(UnpackedParamType<*>::id))
        val cacheTransmitKeys = cache.filterTransmitKeys().filterNot(updateParamKeys::contains)
        val updateTransmitKeys =
            updates.filter(UnpackedParamType<*>::transmit).map(UnpackedParamType<*>::id)
        return IntOpenHashSet(cacheTransmitKeys + updateTransmitKeys)
    }

    /**
     * Combines and returns a set of all unique [UnpackedVarpType.id] values where the
     * [UnpackedVarpType.transmit] level is **not** [VarpTransmitLevel.Never].
     *
     * This key set is primarily used for validating varbits, which reference their parent varp via
     * [VarBitType.baseVar]. Varbits may reference newly built or edited varps whose transmission
     * levels are not yet finalized in the [cache], requiring an up-to-date set.
     *
     * Discrepancies between the existing [cache] and [updates] are resolved by:
     * - Removing outdated transmit flags from [cache] that are overridden by [updates].
     * - Including only updated or valid transmit keys from [updates].
     *
     * This ensures that varps marked as transmissible in [cache] but later updated to
     * [VarpTransmitLevel.Never] in [updates] are excluded, avoiding incorrect inclusion in the
     * final `transmit` keys.
     */
    private fun transmitVarpKeys(cache: VarpTypeList, updates: List<UnpackedVarpType>): Set<Int> {
        val updateVarpKeys = IntOpenHashSet(updates.map(UnpackedVarpType::id))
        val cacheTransmitKeys = cache.filterTransmitKeys().filterNot(updateVarpKeys::contains)
        val updateTransmitKeys = updates.filterNot { it.transmit.never }.map(UnpackedVarpType::id)
        return IntOpenHashSet(cacheTransmitKeys + updateTransmitKeys)
    }

    /**
     * Combines and returns a set of all unique [UnpackedDbTableType.id] values where the
     * [UnpackedDbTableType.clientSide] flag is `true`.
     *
     * This ensures only client-accessible db tables are included for client cache packing,
     * filtering out server-only tables that should not be transmitted to clients.
     *
     * Discrepancies between the existing [cache] and [updates] are resolved by:
     * - Removing outdated `clientSide` flags from [cache] that are overridden by [updates].
     * - Including only updated or valid `clientSide` keys from [updates].
     *
     * This guarantees that db tables marked as `clientSide` in [cache] but later updated to `false`
     * in [updates] are excluded, avoiding incorrect inclusion in the client-transmittable db table
     * keys.
     */
    private fun transmitTableKeys(
        cache: DbTableTypeList,
        updates: List<UnpackedDbTableType>,
    ): Set<Int> {
        val updateTableKeys = IntOpenHashSet(updates.map(UnpackedDbTableType::id))
        val cacheTransmitKeys = cache.filterTransmitKeys().filterNot(updateTableKeys::contains)
        val updateTransmitKeys =
            updates.filter(UnpackedDbTableType::clientSide).map(UnpackedDbTableType::id)
        return IntOpenHashSet(cacheTransmitKeys + updateTransmitKeys)
    }

    private fun collectUpdateMap(vanilla: TypeListMap): UpdateMap {
        val build = builders.resultValues.toUpdateMap()
        val edit = editors.resultValues.toUpdateMap()

        val invs = merge(build.invs, edit.invs, vanilla.invs, InvTypeBuilder)
        val locs = merge(build.locs, edit.locs, vanilla.locs, LocTypeBuilder)
        val npcs = merge(build.npcs, edit.npcs, vanilla.npcs, NpcTypeBuilder)
        val objs = merge(build.objs, edit.objs, vanilla.objs, ObjTypeBuilder)
        val hunt = merge(build.hunt, edit.hunt, vanilla.hunt, HuntModeTypeBuilder)
        val areas = merge(build.areas, edit.areas, vanilla.areas, AreaTypeBuilder)
        val enums = merge(build.enums, edit.enums, vanilla.enums, EnumTypeBuilder)
        val stats = merge(build.stats, edit.stats, vanilla.stats, StatTypeBuilder)
        val varns = merge(build.varns, edit.varns, vanilla.varns, VarnTypeBuilder)
        val varps = merge(build.varps, edit.varps, vanilla.varps, VarpTypeBuilder)
        val dbRows = merge(build.dbRows, edit.dbRows, vanilla.dbRows, DbRowTypeBuilder)
        val params = merge(build.params, edit.params, vanilla.params, ParamTypeBuilder)
        val varbits = merge(build.varbits, edit.varbits, vanilla.varbits, VarBitTypeBuilder)
        val dbTables = merge(build.dbTables, edit.dbTables, vanilla.dbTables, DbTableTypeBuilder)
        val varnbits = merge(build.varnbits, edit.varnbits, vanilla.varnbits, VarnBitTypeBuilder)
        val headbars = merge(build.headbars, edit.headbars, vanilla.headbars, HeadbarTypeBuilder)
        val hitmarks = merge(build.hitmarks, edit.hitmarks, vanilla.hitmarks, HitmarkTypeBuilder)

        val projanims =
            merge(build.projanims, edit.projanims, vanilla.projanims, ProjAnimTypeBuilder)

        val walkTriggers =
            merge(
                build.walkTriggers,
                edit.walkTriggers,
                vanilla.walkTriggers,
                WalkTriggerTypeBuilder,
            )

        return UpdateMap(
            invs = invs,
            locs = locs,
            npcs = npcs,
            objs = objs,
            hunt = hunt,
            stats = stats,
            areas = areas,
            enums = enums,
            params = params,
            varps = varps,
            varbits = varbits,
            varns = varns,
            varnbits = varnbits,
            headbars = headbars,
            hitmarks = hitmarks,
            projanims = projanims,
            walkTriggers = walkTriggers,
            dbRows = dbRows,
            dbTables = dbTables,
        )
    }

    private data class UpdateMap(
        val invs: List<UnpackedInvType>,
        val locs: List<UnpackedLocType>,
        val npcs: List<UnpackedNpcType>,
        val objs: List<UnpackedObjType>,
        val stats: List<UnpackedStatType>,
        val areas: List<UnpackedAreaType>,
        val hunt: List<UnpackedHuntModeType>,
        val enums: List<UnpackedEnumType<*, *>>,
        val params: List<UnpackedParamType<*>>,
        val varps: List<UnpackedVarpType>,
        val varbits: List<UnpackedVarBitType>,
        val varns: List<UnpackedVarnType>,
        val varnbits: List<UnpackedVarnBitType>,
        val headbars: List<UnpackedHeadbarType>,
        val hitmarks: List<UnpackedHitmarkType>,
        val projanims: List<UnpackedProjAnimType>,
        val walkTriggers: List<WalkTriggerType>,
        val dbRows: List<UnpackedDbRowType>,
        val dbTables: List<UnpackedDbTableType>,
    )

    private fun List<*>.toUpdateMap(): UpdateMap {
        val invs = filterIsInstance<UnpackedInvType>()
        val locs = filterIsInstance<UnpackedLocType>()
        val npcs = filterIsInstance<UnpackedNpcType>()
        val objs = filterIsInstance<UnpackedObjType>()
        val stats = filterIsInstance<UnpackedStatType>()
        val areas = filterIsInstance<UnpackedAreaType>()
        val hunt = filterIsInstance<UnpackedHuntModeType>()
        val enums = filterIsInstance<UnpackedEnumType<*, *>>()
        val params = filterIsInstance<UnpackedParamType<*>>()
        val varps = filterIsInstance<UnpackedVarpType>()
        val varbits = filterIsInstance<UnpackedVarBitType>()
        val varns = filterIsInstance<UnpackedVarnType>()
        val varnbits = filterIsInstance<UnpackedVarnBitType>()
        val headbars = filterIsInstance<UnpackedHeadbarType>()
        val hitmarks = filterIsInstance<UnpackedHitmarkType>()
        val projanims = filterIsInstance<UnpackedProjAnimType>()
        val walkTrig = filterIsInstance<WalkTriggerType>()
        val dbRows = filterIsInstance<UnpackedDbRowType>()
        val dbTables = filterIsInstance<UnpackedDbTableType>()

        return UpdateMap(
            invs = invs,
            locs = locs,
            npcs = npcs,
            objs = objs,
            hunt = hunt,
            stats = stats,
            areas = areas,
            enums = enums,
            params = params,
            varps = varps,
            varbits = varbits,
            varns = varns,
            varnbits = varnbits,
            headbars = headbars,
            hitmarks = hitmarks,
            projanims = projanims,
            walkTriggers = walkTrig,
            dbRows = dbRows,
            dbTables = dbTables,
        )
    }

    private fun <T : CacheType> merge(
        builders: List<T>,
        editors: List<T>,
        cacheTypes: Map<Int, T>,
        merger: MergeableCacheBuilder<T>,
    ): List<T> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> merger.merge(next, curr) }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                merger.merge(combined, cacheType)
            } else {
                combined
            }
        }
    }

    private fun encodeCacheTypes(updates: UpdateMap, cachePath: Path, ctx: EncoderContext) {
        Cache.open(cachePath).use { cache ->
            ParamTypeEncoder.encodeAll(cache, updates.params, ctx)
            AreaTypeEncoder.encodeAll(cache, updates.areas, ctx)
            EnumTypeEncoder.encodeAll(cache, updates.enums, ctx)
            InvTypeEncoder.encodeAll(cache, updates.invs, ctx)
            LocTypeEncoder.encodeAll(cache, updates.locs, ctx)
            NpcTypeEncoder.encodeAll(cache, updates.npcs, ctx)
            ObjTypeEncoder.encodeAll(cache, updates.objs, ctx)
            StatTypeEncoder.encodeAll(cache, updates.stats, ctx)
            VarpTypeEncoder.encodeAll(cache, updates.varps, ctx)
            VarBitTypeEncoder.encodeAll(cache, updates.varbits, ctx)
            VarnTypeEncoder.encodeAll(cache, updates.varns, ctx)
            VarnBitTypeEncoder.encodeAll(cache, updates.varnbits, ctx)
            HeadbarTypeEncoder.encodeAll(cache, updates.headbars)
            HitmarkTypeEncoder.encodeAll(cache, updates.hitmarks)
            ProjAnimTypeEncoder.encodeAll(cache, updates.projanims, ctx)
            WalkTriggerTypeEncoder.encodeAll(cache, updates.walkTriggers, ctx)
            DbTableTypeEncoder.encodeAll(cache, updates.dbTables, ctx)
            DbRowTypeEncoder.encodeAll(cache, updates.dbRows, ctx)
            HuntModeTypeEncoder.encodeAll(cache, updates.hunt, ctx)
        }
    }
}
