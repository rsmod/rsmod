package org.runerealm.api.type.builders.hitmark

import jakarta.inject.Inject
import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.builders.resolver.TypeBuilderResolver
import org.runerealm.api.type.builders.resolver.TypeBuilderResult
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.runerealm.api.type.builders.resolver.err
import org.runerealm.api.type.builders.resolver.ok
import org.runerealm.api.type.builders.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.hitmark.HitmarkTypeBuilder
import org.runerealm.game.type.hitmark.HitmarkTypeList
import org.runerealm.game.type.hitmark.UnpackedHitmarkType

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
