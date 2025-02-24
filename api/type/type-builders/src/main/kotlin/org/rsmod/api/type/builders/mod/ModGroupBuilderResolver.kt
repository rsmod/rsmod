package org.rsmod.api.type.builders.mod

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.script.dsl.ModGroupPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.mod.ModGroup

public class ModGroupBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<ModGroupPluginBuilder, ModGroup> {
    private val names: Map<String, Int>
        get() = nameMapping.modGroups

    override fun resolve(
        builders: TypeBuilder<ModGroupPluginBuilder, ModGroup>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun ModGroup.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
