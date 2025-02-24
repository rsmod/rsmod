package org.rsmod.api.type.refs.jingle

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
import org.rsmod.game.type.jingle.JingleType

public class JingleReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<JingleType> {
    private val names: Map<String, Int>
        get() = nameMapping.jingles

    override fun resolve(refs: NameTypeReferences<JingleType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun JingleType.resolve(): TypeReferenceResult {
        val internalId = names[internalName] ?: return err(ImplicitNameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
