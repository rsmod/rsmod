package org.runerealm.api.type.builders.headbar

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
import org.runerealm.game.type.headbar.HeadbarTypeBuilder
import org.runerealm.game.type.headbar.HeadbarTypeList
import org.runerealm.game.type.headbar.UnpackedHeadbarType

public class HeadbarBuilderResolver
@Inject
constructor(private val types: HeadbarTypeList, private val nameMapping: NameMapping) :
    TypeBuilderResolver<HeadbarTypeBuilder, UnpackedHeadbarType> {
    private val names: Map<String, Int>
        get() = nameMapping.headbars

    override fun resolve(
        builders: TypeBuilder<HeadbarTypeBuilder, UnpackedHeadbarType>
    ): List<TypeBuilderResult> = builders.cache.map { it.resolve() }

    private fun UnpackedHeadbarType.resolve(): TypeBuilderResult {
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
