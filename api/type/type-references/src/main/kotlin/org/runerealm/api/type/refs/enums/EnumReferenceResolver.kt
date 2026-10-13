package org.runerealm.api.type.refs.enums

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.type.refs.TypeReferences
import org.runerealm.api.type.refs.resolver.TypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeHashMismatch
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.CacheTypeNotFound
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.InvalidImplicitName
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.KeyTypeMismatch
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.KeyValTypeMismatch
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.NameNotFound
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.ValTypeMismatch
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.issue
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.refs.resolver.update
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.game.type.enums.HashedEnumType
import org.runerealm.game.type.literal.CacheVarTypeMap
import org.runerealm.game.type.literal.CacheVarTypeMap.codecOut

public class EnumReferenceResolver
@Inject
constructor(private val nameMapping: NameMapping, private val types: EnumTypeList) :
    TypeReferenceResolver<HashedEnumType<*, *>, Nothing> {
    private val logger = InlineLogger()

    private val names: Map<String, Int>
        get() = nameMapping.enums

    override fun resolve(
        refs: TypeReferences<HashedEnumType<*, *>, Nothing>
    ): List<TypeReferenceResult> = refs.cache.map { it.resolve() }

    private fun HashedEnumType<*, *>.resolve(): TypeReferenceResult {
        val name = internalName ?: return err(InvalidImplicitName)
        val internalId = names[name] ?: return err(NameNotFound(name, supposedHash))
        TypeResolver[this] = internalId

        val cacheType = types[internalId] ?: return update(CacheTypeNotFound)

        val cacheKeyLiteral = cacheType.keyLiteral.codecOut
        val thisKeyLiteral = CacheVarTypeMap.classedLiterals[keyType]?.codecOut
        val keyTypeMismatch = cacheKeyLiteral != thisKeyLiteral

        val cacheValLiteral = cacheType.valLiteral.codecOut
        val thisValLiteral = CacheVarTypeMap.classedLiterals[valType]?.codecOut
        val valTypeMismatch = cacheValLiteral != thisValLiteral

        if (keyTypeMismatch && valTypeMismatch) {
            val mismatch =
                KeyValTypeMismatch(cacheKeyLiteral, thisKeyLiteral, cacheValLiteral, thisValLiteral)
            return err(mismatch)
        }

        if (keyTypeMismatch) {
            return err(KeyTypeMismatch(cacheKeyLiteral, thisKeyLiteral))
        }

        if (valTypeMismatch) {
            return err(ValTypeMismatch(cacheValLiteral, thisValLiteral))
        }

        val cacheIdentityHash = cacheType.computeIdentityHash()
        if (autoResolve) {
            TypeResolver[this] = cacheIdentityHash
            logger.trace { "  Enum($name) identity hash auto-resolved: $cacheIdentityHash" }
            return ok(FullSuccess)
        }
        if (cacheIdentityHash != supposedHash) {
            return issue(CacheTypeHashMismatch(supposedHash, cacheIdentityHash))
        }
        return ok(FullSuccess)
    }
}
