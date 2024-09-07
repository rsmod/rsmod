package org.rsmod.api.type.refs.mod

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
import org.rsmod.game.type.mod.ModLevel

public class ModLevelReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<ModLevel> {
    private val names: Map<String, Int>
        get() = nameMapping.modLevels

    override fun resolve(refs: NameTypeReferences<ModLevel>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun ModLevel.resolve(): TypeReferenceResult {
        val internalId = names[internalNameGet] ?: return err(ImplicitNameNotFound(internalNameGet))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
