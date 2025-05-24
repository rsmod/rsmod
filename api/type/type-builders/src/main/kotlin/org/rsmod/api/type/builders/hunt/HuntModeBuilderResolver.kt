package org.rsmod.api.type.builders.hunt

import jakarta.inject.Inject
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
import org.rsmod.game.type.hunt.HuntModeTypeBuilder
import org.rsmod.game.type.hunt.HuntModeTypeList
import org.rsmod.game.type.hunt.UnpackedHuntModeType

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
