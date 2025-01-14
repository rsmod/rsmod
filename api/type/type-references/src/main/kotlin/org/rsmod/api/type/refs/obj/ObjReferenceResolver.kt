package org.rsmod.api.type.refs.obj

import com.github.michaelbull.logging.InlineLogger
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
import org.rsmod.game.type.obj.HashedObjType
import org.rsmod.game.type.obj.ObjTypeList

public class ObjReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: ObjTypeList) :
    HashTypeReferenceResolver<HashedObjType> {
    private val logger = InlineLogger()

    private val names: Map<String, Int>
        get() = nameMapping.objs

    override fun resolve(refs: HashTypeReferences<HashedObjType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedObjType.resolve(): TypeReferenceResult {
        val name = internalNameGet ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(ImplicitNameNotFound(name))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        val cacheIdentityHash = cacheType.computeIdentityHash()
        if (supposedHash == null) {
            TypeResolver[this] = cacheIdentityHash
            logger.trace { "  Obj($name) identity hash auto-resolved: $cacheIdentityHash" }
            return ok(FullSuccess)
        }
        if (cacheIdentityHash != supposedHash) {
            return issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
        return ok(FullSuccess)
    }
}
