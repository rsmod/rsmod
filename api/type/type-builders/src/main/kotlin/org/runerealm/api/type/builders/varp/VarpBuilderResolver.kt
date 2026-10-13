package org.runerealm.api.type.builders.varp

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
import org.runerealm.game.type.varp.UnpackedVarpType
import org.runerealm.game.type.varp.VarpTypeBuilder
import org.runerealm.game.type.varp.VarpTypeList

public class VarpBuilderResolver
@Inject
constructor(private val types: VarpTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<VarpTypeBuilder, UnpackedVarpType> {
    private val names: Map<String, Int>
        get() = nameMapping.varps

    override fun resolve(
        builders: TypeBuilder<VarpTypeBuilder, UnpackedVarpType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedVarpType.resolve(): TypeBuilderResult {
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
