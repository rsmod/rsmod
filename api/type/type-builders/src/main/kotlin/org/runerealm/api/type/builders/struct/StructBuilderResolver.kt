package org.runerealm.api.type.builders.struct

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
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.struct.StructTypeBuilder
import org.runerealm.game.type.struct.StructTypeList
import org.runerealm.game.type.struct.UnpackedStructType

public class StructBuilderResolver
@Inject
constructor(private val types: StructTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<StructTypeBuilder, UnpackedStructType> {
    private val names: Map<String, Int>
        get() = nameMapping.structs

    override fun resolve(
        builders: TypeBuilder<StructTypeBuilder, UnpackedStructType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedStructType.resolve(): TypeBuilderResult {
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
