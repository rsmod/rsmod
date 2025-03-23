package org.rsmod.api.type.updater

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import org.openrs2.cache.Cache
import org.rsmod.annotations.EnrichedCache
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.annotations.VanillaCache
import org.rsmod.api.cache.types.TypeListMapDecoder
import org.rsmod.api.cache.types.enums.EnumTypeEncoder
import org.rsmod.api.cache.types.headbar.HeadbarTypeEncoder
import org.rsmod.api.cache.types.hitmark.HitmarkTypeEncoder
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
import org.rsmod.game.type.enums.EnumTypeBuilder
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.UnpackedHeadbarType
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.UnpackedHitmarkType
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
import org.rsmod.game.type.varbit.VarBitTypeBuilder
import org.rsmod.game.type.varn.UnpackedVarnType
import org.rsmod.game.type.varn.VarnTypeBuilder
import org.rsmod.game.type.varnbit.UnpackedVarnBitType
import org.rsmod.game.type.varnbit.VarnBitTypeBuilder
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpTypeBuilder
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder

public class TypeUpdater
@Inject
constructor(
    @EnrichedCache private val enrichedCache: Cache,
    @EnrichedCache private val enrichedCachePath: Path,
    @VanillaCache private val vanillaCachePath: Path,
    @GameCache private val gameCachePath: Path,
    @Js5Cache private val js5CachePath: Path,
    private val names: NameMapping,
    private val builders: TypeBuilderResolverMap,
    private val editors: TypeEditorResolverMap,
) {
    public fun updateAll() {
        overwriteCachePaths()
        encodeAllCacheTypes()
    }

    private fun overwriteCachePaths() {
        deleteExistingCache(gameCachePath)
        copyCache(enrichedCachePath, gameCachePath)

        deleteExistingCache(js5CachePath)
        copyCache(vanillaCachePath, js5CachePath)
    }

    @OptIn(ExperimentalPathApi::class)
    private fun deleteExistingCache(cachePath: Path) {
        cachePath.deleteRecursively()
        check(!cachePath.exists())
    }

    @OptIn(ExperimentalPathApi::class)
    private fun copyCache(from: Path, dest: Path) {
        from.copyToRecursively(target = dest, followLinks = true, overwrite = false)
    }

    private fun encodeAllCacheTypes() {
        val configs = TypeListMapDecoder.ofParallel(enrichedCache, names)
        val updates = collectUpdateMap(configs)
        val params = transmitParamKeys(configs.params, updates.params)
        encodeCacheTypes(updates, gameCachePath, EncoderContext(encodeFull = true, params))
        encodeCacheTypes(updates, js5CachePath, EncoderContext(encodeFull = false, params))
    }

    /**
     * Combines and returns a set of all unique [UnpackedParamType.id] values where the
     * [UnpackedParamType.transmit] flag is `true`. This ensures only relevant parameters are
     * included for client cache packing, filtering out server-side parameters.
     *
     * Discrepancies between the existing [cache] and [updates] are resolved by:
     * - Removing outdated `transmit` flags from [cache] that are overridden by [updates].
     * - Including only updated or valid `transmit` keys from [updates].
     *
     * This guarantees that parameters marked with `transmit` in [cache] but later updated to
     * `false` in [updates] are excluded, avoiding incorrect inclusion in the transmit keys.
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

    private fun collectUpdateMap(vanilla: TypeListMap): UpdateMap {
        val build = builders.resultValues.toUpdateMap()
        val edit = editors.resultValues.toUpdateMap()

        val invs = merge(build.invs, edit.invs, vanilla.invs, InvTypeBuilder)
        val locs = merge(build.locs, edit.locs, vanilla.locs, LocTypeBuilder)
        val npcs = merge(build.npcs, edit.npcs, vanilla.npcs, NpcTypeBuilder)
        val objs = merge(build.objs, edit.objs, vanilla.objs, ObjTypeBuilder)
        val enums = merge(build.enums, edit.enums, vanilla.enums, EnumTypeBuilder)
        val stats = merge(build.stats, edit.stats, vanilla.stats, StatTypeBuilder)
        val varns = merge(build.varns, edit.varns, vanilla.varns, VarnTypeBuilder)
        val varps = merge(build.varps, edit.varps, vanilla.varps, VarpTypeBuilder)
        val params = merge(build.params, edit.params, vanilla.params, ParamTypeBuilder)
        val varbits = merge(build.varbits, edit.varbits, vanilla.varbits, VarBitTypeBuilder)
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
            stats = stats,
            params = params,
            enums = enums,
            varps = varps,
            varbits = varbits,
            varns = varns,
            varnbits = varnbits,
            headbars = headbars,
            hitmarks = hitmarks,
            projanims = projanims,
            walkTriggers = walkTriggers,
        )
    }

    private data class UpdateMap(
        val invs: List<UnpackedInvType>,
        val locs: List<UnpackedLocType>,
        val npcs: List<UnpackedNpcType>,
        val objs: List<UnpackedObjType>,
        val stats: List<UnpackedStatType>,
        val params: List<UnpackedParamType<*>>,
        val enums: List<UnpackedEnumType<*, *>>,
        val varps: List<UnpackedVarpType>,
        val varbits: List<UnpackedVarBitType>,
        val varns: List<UnpackedVarnType>,
        val varnbits: List<UnpackedVarnBitType>,
        val headbars: List<UnpackedHeadbarType>,
        val hitmarks: List<UnpackedHitmarkType>,
        val projanims: List<UnpackedProjAnimType>,
        val walkTriggers: List<WalkTriggerType>,
    )

    private fun List<*>.toUpdateMap(): UpdateMap {
        val invs = filterIsInstance<UnpackedInvType>()
        val locs = filterIsInstance<UnpackedLocType>()
        val npcs = filterIsInstance<UnpackedNpcType>()
        val objs = filterIsInstance<UnpackedObjType>()
        val stats = filterIsInstance<UnpackedStatType>()
        val params = filterIsInstance<UnpackedParamType<*>>()
        val enums = filterIsInstance<UnpackedEnumType<*, *>>()
        val varps = filterIsInstance<UnpackedVarpType>()
        val varbits = filterIsInstance<UnpackedVarBitType>()
        val varns = filterIsInstance<UnpackedVarnType>()
        val varnbits = filterIsInstance<UnpackedVarnBitType>()
        val headbars = filterIsInstance<UnpackedHeadbarType>()
        val hitmarks = filterIsInstance<UnpackedHitmarkType>()
        val projanims = filterIsInstance<UnpackedProjAnimType>()
        val walkTrig = filterIsInstance<WalkTriggerType>()

        return UpdateMap(
            invs = invs,
            locs = locs,
            npcs = npcs,
            objs = objs,
            stats = stats,
            params = params,
            enums = enums,
            varps = varps,
            varbits = varbits,
            varns = varns,
            varnbits = varnbits,
            headbars = headbars,
            hitmarks = hitmarks,
            projanims = projanims,
            walkTriggers = walkTrig,
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
        }
    }
}
