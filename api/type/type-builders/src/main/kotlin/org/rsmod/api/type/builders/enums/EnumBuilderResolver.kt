package org.rsmod.api.type.builders.enums

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
import org.rsmod.api.type.script.dsl.EnumPluginBuilder
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.enums.UnpackedEnumType

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
