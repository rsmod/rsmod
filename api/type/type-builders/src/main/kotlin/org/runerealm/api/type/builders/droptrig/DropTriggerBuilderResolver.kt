package org.runerealm.api.type.builders.droptrig

import jakarta.inject.Inject
import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.builders.resolver.TypeBuilderResolver
import org.runerealm.api.type.builders.resolver.TypeBuilderResult
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.runerealm.api.type.builders.resolver.err
import org.runerealm.api.type.builders.resolver.ok
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.droptrig.DropTriggerType
import org.runerealm.game.type.droptrig.DropTriggerTypeBuilder

public class DropTriggerBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<DropTriggerTypeBuilder, DropTriggerType> {
    private val names: Map<String, Int>
        get() = nameMapping.dropTriggers

    override fun resolve(
        builders: TypeBuilder<DropTriggerTypeBuilder, DropTriggerType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun DropTriggerType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
