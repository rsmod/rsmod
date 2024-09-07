package org.rsmod.api.type.refs.category

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
import org.rsmod.game.type.category.CategoryType

public class CategoryReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<CategoryType> {
    private val names: Map<String, Int>
        get() = nameMapping.categories

    override fun resolve(refs: NameTypeReferences<CategoryType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun CategoryType.resolve(): TypeReferenceResult {
        val internalId = names[internalNameGet] ?: return err(ImplicitNameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
