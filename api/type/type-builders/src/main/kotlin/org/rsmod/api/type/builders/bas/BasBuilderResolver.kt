package org.rsmod.api.type.builders.bas

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
import org.rsmod.game.type.bas.BasType
import org.rsmod.game.type.bas.BasTypeBuilder

public class BasBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<BasTypeBuilder, BasType> {
    private val names: Map<String, Int>
        get() = nameMapping.bas

    override fun resolve(builders: TypeBuilder<BasTypeBuilder, BasType>): List<TypeBuilderResult> =
        builders.cache.map { it.resolve() }

    private fun BasType.resolve(): TypeBuilderResult {
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
