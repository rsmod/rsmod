package org.runerealm.api.type.builders.bas

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
import org.runerealm.game.type.bas.BasType
import org.runerealm.game.type.bas.BasTypeBuilder

public class BasBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<BasTypeBuilder, BasType> {
    private val names: Map<String, Int>
        get() = nameMapping.bas

    override fun resolve(builders: TypeBuilder<BasTypeBuilder, BasType>): List<TypeBuilderResult> =
        builders.cache.map { it.resolve() }

    private fun BasType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
