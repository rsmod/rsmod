package org.rsmod.api.type.builders.droptrig

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.droptrig.DropTriggerTypeBuilder

public class DropTriggerBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<DropTriggerTypeBuilder, DropTriggerType> {
    private val names: Map<String, Int>
        get() = nameMapping.dropTriggers

    override fun resolve(
        builders: TypeBuilder<DropTriggerTypeBuilder, DropTriggerType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun DropTriggerType.resolve(): TypeBuilderResult {
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
