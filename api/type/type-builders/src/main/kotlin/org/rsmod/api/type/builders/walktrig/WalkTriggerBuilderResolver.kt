package org.rsmod.api.type.builders.walktrig

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
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder
import org.rsmod.game.type.walktrig.WalkTriggerTypeList

public class WalkTriggerBuilderResolver
@Inject
constructor(private val types: WalkTriggerTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<WalkTriggerTypeBuilder, WalkTriggerType> {
    private val names: Map<String, Int>
        get() = nameMapping.walkTriggers

    override fun resolve(
        builders: TypeBuilder<WalkTriggerTypeBuilder, WalkTriggerType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun WalkTriggerType.resolve(): TypeBuilderResult {
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
