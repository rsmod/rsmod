package org.runerealm.api.type.builders.walktrig

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
import org.runerealm.game.type.walktrig.WalkTriggerType
import org.runerealm.game.type.walktrig.WalkTriggerTypeBuilder
import org.runerealm.game.type.walktrig.WalkTriggerTypeList

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
