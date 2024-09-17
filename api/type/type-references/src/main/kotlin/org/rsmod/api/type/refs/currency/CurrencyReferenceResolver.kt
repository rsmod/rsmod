package org.rsmod.api.type.refs.currency

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
import org.rsmod.game.type.currency.CurrencyType

public class CurrencyReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<CurrencyType> {
    private val names: Map<String, Int>
        get() = nameMapping.currencies

    override fun resolve(refs: NameTypeReferences<CurrencyType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun CurrencyType.resolve(): TypeReferenceResult {
        val internalId = names[internalNameGet] ?: return err(ImplicitNameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
