package org.runerealm.api.type.builders.varcon

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
import org.runerealm.game.type.varcon.UnpackedVarConType
import org.runerealm.game.type.varcon.VarConTypeBuilder

public class VarConBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<VarConTypeBuilder, UnpackedVarConType> {
    private val names: Map<String, Int>
        get() = nameMapping.varcons

    override fun resolve(
        builders: TypeBuilder<VarConTypeBuilder, UnpackedVarConType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedVarConType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
