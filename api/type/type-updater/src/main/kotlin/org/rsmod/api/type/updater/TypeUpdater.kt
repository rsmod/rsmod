package org.rsmod.api.type.updater

import io.netty.buffer.Unpooled
import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import org.openrs2.cache.Cache
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.annotations.VanillaCache
import org.rsmod.api.cache.types.TypeListMapDecoder
import org.rsmod.api.cache.types.enums.EnumTypeEncoder
import org.rsmod.api.cache.types.inv.InvTypeEncoder
import org.rsmod.api.cache.types.loc.LocTypeEncoder
import org.rsmod.api.cache.types.npc.NpcTypeEncoder
import org.rsmod.api.cache.types.obj.ObjTypeEncoder
import org.rsmod.api.cache.types.param.ParamTypeEncoder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.enums.EnumTypeBuilder
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamTypeBuilder
import org.rsmod.game.type.param.UnpackedParamType

public class TypeUpdater
@Inject
constructor(
    @VanillaCache private val vanillaCache: Cache,
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
        deleteExistingCaches(gameCachePath)
        deleteExistingCaches(js5CachePath)
        copyVanillaCache(gameCachePath)
        copyVanillaCache(js5CachePath)
    }

    @OptIn(ExperimentalPathApi::class)
    private fun deleteExistingCaches(cachePath: Path) {
        cachePath.deleteRecursively()
        check(!cachePath.exists())
    }

    @OptIn(ExperimentalPathApi::class)
    private fun copyVanillaCache(targetCachePath: Path) {
        vanillaCachePath.copyToRecursively(
            target = targetCachePath,
            followLinks = true,
            overwrite = false,
        )
    }

    private fun encodeAllCacheTypes() {
        val updates = collectUpdateMap()
        encodeCacheTypes(updates, gameCachePath, serverCache = true)
        encodeCacheTypes(updates, js5CachePath, serverCache = false)
    }

    private fun collectUpdateMap(): UpdateMap {
        val configs = TypeListMapDecoder.ofParallel(vanillaCache, names)
        return collectUpdateMap(configs)
    }

    private fun collectUpdateMap(vanilla: TypeListMap): UpdateMap {
        val builders = builders.resultValues.toUpdateMap()
        val editors = editors.resultValues.toUpdateMap()

        val invs = mergeInvs(builders.invs, editors.invs, vanilla.invs)
        val locs = mergeLocs(builders.locs, editors.locs, vanilla.locs)
        val npcs = mergeNpcs(builders.npcs, editors.npcs, vanilla.npcs)
        val objs = mergeObjs(builders.objs, editors.objs, vanilla.objs)
        val params = mergeParams(builders.params, editors.params, vanilla.params)
        val enums = mergeEnums(builders.enums, editors.enums, vanilla.enums)
        return UpdateMap(invs, locs, npcs, objs, params, enums)
    }

    private data class UpdateMap(
        val invs: List<UnpackedInvType>,
        val locs: List<UnpackedLocType>,
        val npcs: List<UnpackedNpcType>,
        val objs: List<UnpackedObjType>,
        val params: List<UnpackedParamType<*>>,
        val enums: List<UnpackedEnumType<*, *>>,
    )

    private fun List<*>.toUpdateMap(): UpdateMap {
        val invs = filterIsInstance<UnpackedInvType>()
        val locs = filterIsInstance<UnpackedLocType>()
        val npcs = filterIsInstance<UnpackedNpcType>()
        val objs = filterIsInstance<UnpackedObjType>()
        val params = filterIsInstance<UnpackedParamType<*>>()
        val enums = filterIsInstance<UnpackedEnumType<*, *>>()
        return UpdateMap(invs, locs, npcs, objs, params, enums)
    }

    private fun mergeInvs(
        builders: List<UnpackedInvType>,
        editors: List<UnpackedInvType>,
        cacheTypes: Map<Int, UnpackedInvType>,
    ): List<UnpackedInvType> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map {
            check(it.value.size <= 4) {
                "A single cache-type can only have up to 4 builders or editors modifying it."
            }
            val combined =
                it.value[0] + it.value.getOrNull(1) + it.value.getOrNull(2) + it.value.getOrNull(3)
            val cacheType = cacheTypes[combined.id]
            if (cacheType != null) {
                cacheType + combined
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
        return merged.map {
            check(it.value.size <= 4) {
                "A single cache-type can only have up to 4 builders or editors modifying it."
            }
            val combined =
                it.value[0] + it.value.getOrNull(1) + it.value.getOrNull(2) + it.value.getOrNull(3)
            val cacheType = cacheTypes[combined.id]
            if (cacheType != null) {
                cacheType + combined
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
        return merged.map {
            check(it.value.size <= 4) {
                "A single cache-type can only have up to 4 builders or editors modifying it."
            }
            val combined =
                it.value[0] + it.value.getOrNull(1) + it.value.getOrNull(2) + it.value.getOrNull(3)
            val cacheType = cacheTypes[combined.id]
            if (cacheType != null) {
                cacheType + combined
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
        return merged.map {
            check(it.value.size <= 4) {
                "A single cache-type can only have up to 4 builders or editors modifying it."
            }
            val combined =
                it.value[0] + it.value.getOrNull(1) + it.value.getOrNull(2) + it.value.getOrNull(3)
            val cacheType = cacheTypes[combined.id]
            if (cacheType != null) {
                cacheType + combined
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

    private fun mergeParams(
        builders: List<UnpackedParamType<*>>,
        editors: List<UnpackedParamType<*>>,
        cacheTypes: Map<Int, UnpackedParamType<*>>,
    ): List<UnpackedParamType<*>> {
        val merged = (builders + editors).groupBy { it.id }
        return merged.map {
            check(it.value.size <= 4) {
                "A single cache-type can only have up to 4 builders or editors modifying it."
            }
            val combined =
                it.value[0] + it.value.getOrNull(1) + it.value.getOrNull(2) + it.value.getOrNull(3)
            val cacheType = cacheTypes[combined.id]
            if (cacheType != null) {
                cacheType + combined
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
        return merged.map {
            check(it.value.size <= 4) {
                "A single cache-type can only have up to 4 builders or editors modifying it."
            }
            val combined =
                it.value[0] + it.value.getOrNull(1) + it.value.getOrNull(2) + it.value.getOrNull(3)
            val cacheType = cacheTypes[combined.id]
            if (cacheType != null) {
                cacheType + combined
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

    private fun encodeCacheTypes(updates: UpdateMap, cachePath: Path, serverCache: Boolean) {
        val buffer = Unpooled.buffer()
        Cache.open(cachePath).use { cache ->
            ParamTypeEncoder.encodeAll(cache, updates.params, serverCache, buffer)
            EnumTypeEncoder.encodeAll(cache, updates.enums, serverCache, buffer)
            InvTypeEncoder.encodeAll(cache, updates.invs, serverCache, buffer)
            LocTypeEncoder.encodeAll(cache, updates.locs, serverCache, buffer)
            NpcTypeEncoder.encodeAll(cache, updates.npcs, serverCache, buffer)
            ObjTypeEncoder.encodeAll(cache, updates.objs, serverCache, buffer)
        }
        buffer.release()
    }
}
