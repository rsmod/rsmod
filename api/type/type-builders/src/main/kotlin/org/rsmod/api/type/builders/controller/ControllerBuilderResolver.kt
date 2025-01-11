package org.rsmod.api.type.builders.controller

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
import org.rsmod.game.type.controller.ControllerType
import org.rsmod.game.type.controller.ControllerTypeBuilder

public class ControllerBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<ControllerTypeBuilder, ControllerType> {
    private val names: Map<String, Int>
        get() = nameMapping.controllers

    override fun resolve(
        builders: TypeBuilder<ControllerTypeBuilder, ControllerType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun ControllerType.resolve(): TypeBuilderResult {
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
