package org.rsmod.api.type.builders.stat

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
import org.rsmod.game.type.stat.StatTypeBuilder
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.stat.UnpackedStatType

public class StatBuilderResolver
@Inject
constructor(private val types: StatTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<StatTypeBuilder, UnpackedStatType> {
    private val names: Map<String, Int>
        get() = nameMapping.stats

    override fun resolve(
        builders: TypeBuilder<StatTypeBuilder, UnpackedStatType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedStatType.resolve(): TypeBuilderResult {
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
