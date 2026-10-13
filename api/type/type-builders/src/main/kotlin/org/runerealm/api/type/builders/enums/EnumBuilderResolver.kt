package org.runerealm.api.type.builders.enums

import jakarta.inject.Inject
import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.builders.resolver.TypeBuilderResolver
import org.runerealm.api.type.builders.resolver.TypeBuilderResult
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.runerealm.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.runerealm.api.type.builders.resolver.err
import org.runerealm.api.type.builders.resolver.ok
import org.runerealm.api.type.builders.resolver.update
import org.runerealm.api.type.script.dsl.EnumPluginBuilder
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.game.type.enums.UnpackedEnumType

public class EnumBuilderResolver
@Inject
constructor(private val types: EnumTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<EnumPluginBuilder<*, *>, UnpackedEnumType<*, *>> {
    private val names: Map<String, Int>
        get() = nameMapping.enums

    override fun resolve(
        builders: TypeBuilder<EnumPluginBuilder<*, *>, UnpackedEnumType<*, *>>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedEnumType<*, *>.resolve(): TypeBuilderResult {
        val internalId = names[internalName] ?: return err(NameNotFound(internalName))
        val cacheType = types[internalId]

        TypeResolver[this] = internalId

        return if (cacheType != this) {
            update(CachePackRequired)
        } else {
            ok(FullSuccess)
        }
    }
}
