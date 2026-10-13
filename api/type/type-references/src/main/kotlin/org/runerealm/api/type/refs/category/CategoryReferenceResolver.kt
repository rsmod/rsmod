package org.runerealm.api.type.refs.category

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
import org.runerealm.game.type.category.CategoryType

public class CategoryReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<CategoryType> {
    private val names: Map<String, Int>
        get() = nameMapping.categories

    override fun resolve(refs: NameTypeReferences<CategoryType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun CategoryType.resolve(): TypeReferenceResult {
        val internalId = names[internalName] ?: return err(ImplicitNameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
