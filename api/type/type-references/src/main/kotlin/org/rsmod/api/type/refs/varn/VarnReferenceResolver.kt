package org.rsmod.api.type.refs.varn

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
import org.rsmod.game.type.varn.HashedVarnType
import org.rsmod.game.type.varn.VarnTypeList

public class VarnReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: VarnTypeList) :
    NameTypeReferenceResolver<HashedVarnType> {
    private val names: Map<String, Int>
        get() = nameMapping.varns

    override fun resolve(refs: NameTypeReferences<HashedVarnType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedVarnType.resolve(): TypeReferenceResult {
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
