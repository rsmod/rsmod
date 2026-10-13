package org.runerealm.api.type.refs.varbit

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.api.type.refs.resolver.HashTypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.issue
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.refs.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.varbit.HashedVarBitType
import org.runerealm.game.type.varbit.VarBitTypeList

public class VarBitReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: VarBitTypeList) :
    HashTypeReferenceResolver<HashedVarBitType> {
    private val logger = InlineLogger()

    private val names: Map<String, Int>
        get() = nameMapping.varbits

    override fun resolve(refs: HashTypeReferences<HashedVarBitType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedVarBitType.resolve(): TypeReferenceResult {
        val name = internalName ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(NameNotFound(name, supposedHash))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        TypeResolver[this] = cacheType.bits
        TypeResolver[this] = cacheType.baseVar

        val cacheIdentityHash = cacheType.computeIdentityHash()
        if (autoResolve) {
            TypeResolver[this] = cacheIdentityHash
            logger.trace { "  VarBit($name) identity hash auto-resolved: $cacheIdentityHash" }
            return ok(FullSuccess)
        }
        if (cacheIdentityHash != supposedHash) {
            return issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
        return ok(FullSuccess)
    }
}
