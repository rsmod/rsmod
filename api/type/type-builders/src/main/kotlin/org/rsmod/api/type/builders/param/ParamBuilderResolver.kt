package org.rsmod.api.type.builders.param

import jakarta.inject.Inject
import kotlin.collections.get
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.builders.resolver.update
import org.rsmod.api.type.script.dsl.TypedParamPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.param.UnpackedParamType

public class ParamBuilderResolver
@Inject
constructor(private val types: ParamTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<TypedParamPluginBuilder<*>, UnpackedParamType<*>> {
    private val names: Map<String, Int>
        get() = nameMapping.params

    override fun resolve(
        builders: TypeBuilder<TypedParamPluginBuilder<*>, UnpackedParamType<*>>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedParamType<*>.resolve(): TypeBuilderResult {
        val internalId = names[internalNameGet] ?: return err(NameNotFound(internalNameGet))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
