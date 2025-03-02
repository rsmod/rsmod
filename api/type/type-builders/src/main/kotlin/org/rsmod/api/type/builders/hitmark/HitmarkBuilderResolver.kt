package org.rsmod.api.type.builders.hitmark

import jakarta.inject.Inject
import kotlin.collections.get
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.builders.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.HitmarkTypeList
import org.rsmod.game.type.hitmark.UnpackedHitmarkType

public class HitmarkBuilderResolver
@Inject
constructor(private val types: HitmarkTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<HitmarkTypeBuilder, UnpackedHitmarkType> {
    private val names: Map<String, Int>
        get() = nameMapping.hitmarks

    override fun resolve(
        builders: TypeBuilder<HitmarkTypeBuilder, UnpackedHitmarkType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedHitmarkType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
