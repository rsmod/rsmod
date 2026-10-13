package org.runerealm.api.type.builders.controller

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
import org.runerealm.game.type.controller.ControllerType
import org.runerealm.game.type.controller.ControllerTypeBuilder

public class ControllerBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<ControllerTypeBuilder, ControllerType> {
    private val names: Map<String, Int>
        get() = nameMapping.controllers

    override fun resolve(
        builders: TypeBuilder<ControllerTypeBuilder, ControllerType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun ControllerType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
