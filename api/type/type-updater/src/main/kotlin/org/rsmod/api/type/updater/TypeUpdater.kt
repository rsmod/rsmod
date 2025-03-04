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
import org.rsmod.game.type.stat.StatTypeBuilder
import org.rsmod.game.type.stat.UnpackedStatType
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
        val builders = builders.resultValues.toUpdateMap()
        val editors = editors.resultValues.toUpdateMap()

        val invs = mergeInvs(builders.invs, editors.invs, vanilla.invs)
        val locs = mergeLocs(builders.locs, editors.locs, vanilla.locs)
        val npcs = mergeNpcs(builders.npcs, editors.npcs, vanilla.npcs)
        val objs = mergeObjs(builders.objs, editors.objs, vanilla.objs)
        val stats = mergeStats(builders.stats, editors.stats, vanilla.stats)
        val params = mergeParams(builders.params, editors.params, vanilla.params)
        val enums = mergeEnums(builders.enums, editors.enums, vanilla.enums)
        val varps = mergeVarps(builders.varps, editors.varps, vanilla.varps)
        val varbits = mergeVarBits(builders.varbits, editors.varbits, vanilla.varbits)
        val varns = mergeVarns(builders.varns, editors.varns, vanilla.varns)
        val varnbits = mergeVarnBits(builders.varnbits, editors.varnbits, vanilla.varnbits)
        val headbars = mergeHeadbars(builders.headbars, editors.headbars, vanilla.headbars)
        val hitmarks = mergeHitmarks(builders.hitmarks, editors.hitmarks, vanilla.hitmarks)
        val walkTrig = mergeWalkTriggers(builders.walkTrig, editors.walkTrig, vanilla.walkTriggers)

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
            walkTrig = walkTrig,
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
        val walkTrig: List<WalkTriggerType>,
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
            walkTrig = walkTrig,
        )
    }

    private fun mergeInvs(
        builders: List<UnpackedInvType>,
        editors: List<UnpackedInvType>,
        cacheTypes: Map<Int, UnpackedInvType>,
    ): List<UnpackedInvType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedInvType.plus(other: UnpackedInvType?): UnpackedInvType =
        if (other != null) {
            InvTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeLocs(
        builders: List<UnpackedLocType>,
        editors: List<UnpackedLocType>,
        cacheTypes: Map<Int, UnpackedLocType>,
    ): List<UnpackedLocType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedLocType.plus(other: UnpackedLocType?): UnpackedLocType =
        if (other != null) {
            LocTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeNpcs(
        builders: List<UnpackedNpcType>,
        editors: List<UnpackedNpcType>,
        cacheTypes: Map<Int, UnpackedNpcType>,
    ): List<UnpackedNpcType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedNpcType.plus(other: UnpackedNpcType?): UnpackedNpcType =
        if (other != null) {
            NpcTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeObjs(
        builders: List<UnpackedObjType>,
        editors: List<UnpackedObjType>,
        cacheTypes: Map<Int, UnpackedObjType>,
    ): List<UnpackedObjType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedObjType.plus(other: UnpackedObjType?): UnpackedObjType =
        if (other != null) {
            ObjTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeStats(
        builders: List<UnpackedStatType>,
        editors: List<UnpackedStatType>,
        cacheTypes: Map<Int, UnpackedStatType>,
    ): List<UnpackedStatType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedStatType.plus(other: UnpackedStatType?): UnpackedStatType =
        if (other != null) {
            StatTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeParams(
        builders: List<UnpackedParamType<*>>,
        editors: List<UnpackedParamType<*>>,
        cacheTypes: Map<Int, UnpackedParamType<*>>,
    ): List<UnpackedParamType<*>> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedParamType<*>.plus(
        other: UnpackedParamType<*>?
    ): UnpackedParamType<*> =
        if (other != null) {
            ParamTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeEnums(
        builders: List<UnpackedEnumType<*, *>>,
        editors: List<UnpackedEnumType<*, *>>,
        cacheTypes: Map<Int, UnpackedEnumType<*, *>>,
    ): List<UnpackedEnumType<*, *>> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedEnumType<*, *>.plus(
        other: UnpackedEnumType<*, *>?
    ): UnpackedEnumType<*, *> =
        if (other != null) {
            EnumTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeVarBits(
        builders: List<UnpackedVarBitType>,
        editors: List<UnpackedVarBitType>,
        cacheTypes: Map<Int, UnpackedVarBitType>,
    ): List<UnpackedVarBitType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedVarBitType.plus(other: UnpackedVarBitType?): UnpackedVarBitType =
        if (other != null) {
            VarBitTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeVarps(
        builders: List<UnpackedVarpType>,
        editors: List<UnpackedVarpType>,
        cacheTypes: Map<Int, UnpackedVarpType>,
    ): List<UnpackedVarpType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedVarpType.plus(other: UnpackedVarpType?): UnpackedVarpType =
        if (other != null) {
            VarpTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeVarns(
        builders: List<UnpackedVarnType>,
        editors: List<UnpackedVarnType>,
        cacheTypes: Map<Int, UnpackedVarnType>,
    ): List<UnpackedVarnType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedVarnType.plus(other: UnpackedVarnType?): UnpackedVarnType =
        if (other != null) {
            VarnTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeVarnBits(
        builders: List<UnpackedVarnBitType>,
        editors: List<UnpackedVarnBitType>,
        cacheTypes: Map<Int, UnpackedVarnBitType>,
    ): List<UnpackedVarnBitType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedVarnBitType.plus(
        other: UnpackedVarnBitType?
    ): UnpackedVarnBitType =
        if (other != null) {
            VarnBitTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeHeadbars(
        builders: List<UnpackedHeadbarType>,
        editors: List<UnpackedHeadbarType>,
        cacheTypes: Map<Int, UnpackedHeadbarType>,
    ): List<UnpackedHeadbarType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedHeadbarType.plus(
        other: UnpackedHeadbarType?
    ): UnpackedHeadbarType =
        if (other != null) {
            HeadbarTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeHitmarks(
        builders: List<UnpackedHitmarkType>,
        editors: List<UnpackedHitmarkType>,
        cacheTypes: Map<Int, UnpackedHitmarkType>,
    ): List<UnpackedHitmarkType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun UnpackedHitmarkType.plus(
        other: UnpackedHitmarkType?
    ): UnpackedHitmarkType =
        if (other != null) {
            HitmarkTypeBuilder.merge(edit = this, base = other)
        } else {
            this
        }

    private fun mergeWalkTriggers(
        builders: List<WalkTriggerType>,
        editors: List<WalkTriggerType>,
        cacheTypes: Map<Int, WalkTriggerType>,
    ): List<WalkTriggerType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map { (id, types) ->
            val combined = types.fold(types[0]) { curr, next -> next + curr }
            val cacheType = cacheTypes[id]
            if (cacheType != null) {
                combined + cacheType
            } else {
                combined
            }
        }
    }

    private operator fun WalkTriggerType.plus(other: WalkTriggerType?): WalkTriggerType =
        if (other != null) {
            WalkTriggerTypeBuilder.merge(edit = this, base = other)
        } else {
            this
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
            WalkTriggerTypeEncoder.encodeAll(cache, updates.walkTrig, ctx)
        }
    }
}
