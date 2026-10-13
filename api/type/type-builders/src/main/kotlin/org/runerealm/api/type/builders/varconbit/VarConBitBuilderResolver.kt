package org.runerealm.api.type.builders.varconbit

import jakarta.inject.Inject
import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.builders.resolver.TypeBuilderResolver
import org.runerealm.api.type.builders.resolver.TypeBuilderResult
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.runerealm.api.type.builders.resolver.err
import org.runerealm.api.type.builders.resolver.ok
import org.runerealm.api.type.script.dsl.VarConBitPluginBuilder
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.varconbit.UnpackedVarConBitType

public class VarConBitBuilderResolver @Inject constructor(private val nameMapping: NameMapping) :
    TypeBuilderResolver<VarConBitPluginBuilder, UnpackedVarConBitType> {
    private val names: Map<String, Int>
        get() = nameMapping.varconbits

    override fun resolve(
        builders: TypeBuilder<VarConBitPluginBuilder, UnpackedVarConBitType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedVarConBitType.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
