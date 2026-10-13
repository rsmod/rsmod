package org.runerealm.api.type.builders.area

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
import org.runerealm.game.type.area.AreaTypeBuilder
import org.runerealm.game.type.area.AreaTypeList
import org.runerealm.game.type.area.UnpackedAreaType

public class AreaBuilderResolver
@Inject
constructor(private val types: AreaTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<AreaTypeBuilder, UnpackedAreaType> {
    private val names: Map<String, Int>
        get() = nameMapping.areas

    override fun resolve(
        builders: TypeBuilder<AreaTypeBuilder, UnpackedAreaType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedAreaType.resolve(): TypeBuilderResult {
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
