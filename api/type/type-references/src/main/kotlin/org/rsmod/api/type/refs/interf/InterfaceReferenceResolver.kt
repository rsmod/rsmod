package org.rsmod.api.type.refs.interf

import jakarta.inject.Inject
import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.api.type.refs.resolver.HashTypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.HashNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.issue
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.hash.HashMapping
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.interf.HashedInterfaceType
import org.rsmod.game.type.interf.InterfaceTypeList

public class InterfaceReferenceResolver
@Inject
constructor(
    private val nameMapping: NameMapping,
    private val hashMapping: HashMapping,
    private val types: InterfaceTypeList,
) : HashTypeReferenceResolver<HashedInterfaceType> {
    private val names: Map<String, Int>
        get() = nameMapping.interfaces

    private val hashes: Map<Long, String>
        get() = hashMapping.interfaces

    override fun resolve(refs: HashTypeReferences<HashedInterfaceType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedInterfaceType.resolve(): TypeReferenceResult {
        val name = hashes[supposedHash] ?: return err(HashNotFound(supposedHash))
        val internalId = names[name] ?: return err(NameNotFound(name, supposedHash))
        val cacheType = types[internalId]

        TypeResolver[this] = name
        TypeResolver[this] = internalId

        return when (val cacheIdentityHash = cacheType?.computeIdentityHash()) {
            null -> update(CacheTypeNotFound)
            supposedHash -> ok(FullSuccess)
            else -> issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
    }
}
