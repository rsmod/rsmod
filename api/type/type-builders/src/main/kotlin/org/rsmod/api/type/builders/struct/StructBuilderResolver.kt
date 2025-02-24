package org.rsmod.api.type.builders.struct

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolver
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.CachePackRequired
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.FullSuccess
import org.rsmod.api.type.builders.resolver.TypeBuilderResult.NameNotFound
import org.rsmod.api.type.builders.resolver.err
import org.rsmod.api.type.builders.resolver.ok
import org.rsmod.api.type.builders.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.struct.StructTypeBuilder
import org.rsmod.game.type.struct.StructTypeList
import org.rsmod.game.type.struct.UnpackedStructType

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
