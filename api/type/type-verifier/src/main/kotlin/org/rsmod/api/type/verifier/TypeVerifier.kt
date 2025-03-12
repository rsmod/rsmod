package org.rsmod.api.type.verifier

import jakarta.inject.Inject
import kotlin.contracts.contract
import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.api.type.refs.resolver.TypeReferenceResolverMap

public class TypeVerifier
@Inject
constructor(
    private val references: TypeReferenceResolverMap,
    private val builders: TypeBuilderResolverMap,
    private val editors: TypeEditorResolverMap,
) {
    public fun verifyAll(verifyIdentityHashes: Boolean = true): Verification {
        val reference = verifyReferences(verifyIdentityHashes)
        val builder = verifyBuilders()
        val editor = verifyEditors()

        // Order is important - we want cache updates from builders and editors be returned first.
        val verifications = listOf(builder, editor, reference)

        val updates = verifications.filterIsInstance<Verification.CacheUpdateRequired>()
        if (updates.isNotEmpty()) {
            val errorMessage =
                updates.joinToString(separator = System.lineSeparator()) { it.error.message }
            return Verification.CacheUpdateRequired(ResolutionError(errorMessage))
        }

        val failures = verifications.filterIsInstance<Verification.Failure>()
        if (failures.isNotEmpty()) {
            val errorMessage =
                failures.joinToString(separator = System.lineSeparator()) { it.error.message }
            return Verification.Failure(ResolutionError(errorMessage))
        }

        return Verification.FullSuccess
    }

    public fun verifyReferences(verifyIdentityHashes: Boolean): Verification {
        val verifier = ReferenceVerifier(references)
        return verifier.verifyAll(verifyIdentityHashes)
    }

    public fun verifyEditors(): Verification {
        val verifier = EditorVerifier(editors)
        return verifier.verifyAll()
    }

    public fun verifyBuilders(): Verification {
        val verifier = BuilderVerifier(builders)
        return verifier.verifyAll()
    }

    public data class ResolutionError(public val message: String) {
        override fun toString(): String = message
    }

    public sealed class Verification {
        public class Failure(public val error: ResolutionError) : Verification() {
            public fun formatError(): String = System.lineSeparator() + error.toString()
        }

        public class CacheUpdateRequired(public val error: ResolutionError) : Verification() {
            public fun formatError(): String = System.lineSeparator() + error.toString()
        }

        public data object FullSuccess : Verification()

        public fun isSuccess(): Boolean = this == FullSuccess
    }
}

public fun TypeVerifier.Verification?.isCacheUpdateRequired(): Boolean {
    contract {
        returns(true) implies
            (this@isCacheUpdateRequired is TypeVerifier.Verification.CacheUpdateRequired)
    }
    return this is TypeVerifier.Verification.CacheUpdateRequired
}

public fun TypeVerifier.Verification?.isFailure(): Boolean {
    contract { returns(true) implies (this@isFailure is TypeVerifier.Verification.Failure) }
    return this is TypeVerifier.Verification.Failure
}
