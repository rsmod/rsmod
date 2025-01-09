package org.rsmod.api.type.refs.font

import jakarta.inject.Inject
import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.api.type.refs.resolver.HashTypeReferenceResolver
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.rsmod.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.rsmod.api.type.refs.resolver.err
import org.rsmod.api.type.refs.resolver.issue
import org.rsmod.api.type.refs.resolver.ok
import org.rsmod.api.type.refs.resolver.update
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.font.HashedFontMetricsType

public class FontMetricsReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: FontMetricsTypeList) :
    HashTypeReferenceResolver<HashedFontMetricsType> {
    private val names: Map<String, Int>
        get() = nameMapping.fonts

    override fun resolve(
        refs: HashTypeReferences<HashedFontMetricsType>
    ): List<TypeReferenceResult> = refs.cache.map { it.resolve() }

    private fun HashedFontMetricsType.resolve(): TypeReferenceResult {
        val name = internalNameGet ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(NameNotFound(name, supposedHash))
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
