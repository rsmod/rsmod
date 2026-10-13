package org.runerealm.api.type.refs.npc

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.api.type.refs.resolver.HashTypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.issue
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.refs.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.npc.HashedNpcType
import org.runerealm.game.type.npc.NpcTypeList

public class NpcReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: NpcTypeList) :
    HashTypeReferenceResolver<HashedNpcType> {
    private val logger = InlineLogger()

    private val names: Map<String, Int>
        get() = nameMapping.npcs

    override fun resolve(refs: HashTypeReferences<HashedNpcType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun HashedNpcType.resolve(): TypeReferenceResult {
        val name = internalName ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(ImplicitNameNotFound(name))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)
        val cacheIdentityHash = cacheType.computeIdentityHash()
        if (autoResolve) {
            TypeResolver[this] = cacheIdentityHash
            logger.trace { "  Npc($name) identity hash auto-resolved: $cacheIdentityHash" }
            return ok(FullSuccess)
        }
        if (cacheIdentityHash != supposedHash) {
            return issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
        return ok(FullSuccess)
    }
}
