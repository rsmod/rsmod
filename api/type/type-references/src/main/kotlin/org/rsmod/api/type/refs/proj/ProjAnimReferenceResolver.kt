package org.rsmod.api.type.refs.proj

import jakarta.inject.Inject
import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.api.type.refs.resolver.NameTypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.proj.HashedProjAnimType
import org.rsmod.game.type.proj.ProjAnimTypeList

public class ProjAnimReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: ProjAnimTypeList) :
    NameTypeReferenceResolver<HashedProjAnimType> {
    private val names: Map<String, Int>
        get() = nameMapping.projanims

    override fun resolve(refs: NameTypeReferences<HashedProjAnimType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedProjAnimType.resolve(): TypeReferenceResult {
        val name = internalName ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(ImplicitNameNotFound(name))
        TypeResolver[this] = internalId

        val cacheType = types[internalId]
        return if (cacheType == null) {
            update(CacheTypeNotFound)
        } else {
            ok(FullSuccess)
        }
    }
}
