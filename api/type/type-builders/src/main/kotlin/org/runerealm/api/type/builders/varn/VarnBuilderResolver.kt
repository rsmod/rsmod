package org.runerealm.api.type.builders.varn

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
import org.runerealm.game.type.varn.UnpackedVarnType
import org.runerealm.game.type.varn.VarnTypeBuilder
import org.runerealm.game.type.varn.VarnTypeList

public class VarnBuilderResolver
@Inject
constructor(private val types: VarnTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<VarnTypeBuilder, UnpackedVarnType> {
    private val names: Map<String, Int>
        get() = nameMapping.varns

    override fun resolve(
        builders: TypeBuilder<VarnTypeBuilder, UnpackedVarnType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedVarnType.resolve(): TypeBuilderResult {
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
