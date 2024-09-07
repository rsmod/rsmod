package org.rsmod.api.type.refs.content

import jakarta.inject.Inject
import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.api.type.refs.resolver.NameTypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.content.ContentType

public class ContentReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<ContentType> {
    private val names: Map<String, Int>
        get() = nameMapping.content

    override fun resolve(refs: NameTypeReferences<ContentType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun ContentType.resolve(): TypeReferenceResult {
        val internalId = names[internalNameGet] ?: return err(ImplicitNameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
