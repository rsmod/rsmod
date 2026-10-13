package org.runerealm.api.type.builders.hunt

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
import org.runerealm.game.type.hunt.HuntModeTypeBuilder
import org.runerealm.game.type.hunt.HuntModeTypeList
import org.runerealm.game.type.hunt.UnpackedHuntModeType

public class HuntModeBuilderResolver
@Inject
constructor(private val types: HuntModeTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<HuntModeTypeBuilder, UnpackedHuntModeType> {
    private val names: Map<String, Int>
        get() = nameMapping.hunt

    override fun resolve(
        builders: TypeBuilder<HuntModeTypeBuilder, UnpackedHuntModeType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedHuntModeType.resolve(): TypeBuilderResult {
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
