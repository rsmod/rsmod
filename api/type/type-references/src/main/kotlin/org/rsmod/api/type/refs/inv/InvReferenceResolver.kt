package org.rsmod.api.type.refs.inv

import jakarta.inject.Inject
import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.api.type.refs.resolver.HashTypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.issue
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.inv.HashedInvType
import org.rsmod.game.type.inv.InvTypeList

public class InvReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: InvTypeList) :
    HashTypeReferenceResolver<HashedInvType> {
    private val names: Map<String, Int>
        get() = nameMapping.invs

    override fun resolve(refs: HashTypeReferences<HashedInvType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedInvType.resolve(): TypeReferenceResult {
        val name = internalNameGet ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(ImplicitNameNotFound(name))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        if (supposedHash == null) {
            return ok(FullSuccess)
        }
        val cacheIdentityHash = cacheType.computeIdentityHash()
        if (cacheIdentityHash != supposedHash) {
            return issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
        return ok(FullSuccess)
    }
}
