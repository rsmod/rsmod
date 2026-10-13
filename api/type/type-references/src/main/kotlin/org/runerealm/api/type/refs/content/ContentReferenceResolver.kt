package org.runerealm.api.type.refs.content

import jakarta.inject.Inject
import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.api.type.refs.resolver.NameTypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.content.ContentGroupType

public class ContentReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<ContentGroupType> {
    private val names: Map<String, Int>
        get() = nameMapping.content

    override fun resolve(refs: NameTypeReferences<ContentGroupType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun ContentGroupType.resolve(): TypeReferenceResult {
        val internalId = names[internalName] ?: return err(ImplicitNameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
