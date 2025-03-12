package org.rsmod.api.type.verifier

import jakarta.inject.Inject
import org.rsmod.api.type.refs.resolver.TypeReferenceResolverMap
import org.rsmod.api.type.refs.resolver.TypeReferenceResult
import org.rsmod.api.type.verifier.TypeVerifier.ResolutionError
import org.rsmod.api.type.verifier.TypeVerifier.Verification

public class ReferenceVerifier
@Inject
constructor(private val references: TypeReferenceResolverMap) {
    public fun verifyAll(verifyIdentityHashes: Boolean): Verification {
        val updates = references.updates
        if (updates.isNotEmpty()) {
            val error = updates.toUpdateError()
            return Verification.CacheUpdateRequired(error)
        }

        val issues =
            if (!verifyIdentityHashes) {
                references.issues.filter { it.status !is TypeReferenceResult.CacheTypeHashMismatch }
            } else {
                references.issues
            }
        if (issues.isNotEmpty()) {
            val error = issues.toIssueError()
            return Verification.Failure(error)
        }

        val errors = references.errors
        if (errors.isNotEmpty()) {
            val error = errors.toError()
            return Verification.Failure(error)
        }

        return Verification.FullSuccess
    }

    private fun List<TypeReferenceResult.Update<*>>.toUpdateError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toUpdateMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeReferenceResult.Update<*>>.toUpdateMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeReferenceResult.StatusUpdate.toHeader(count: Int): String =
        when (this) {
            TypeReferenceResult.CacheTypeNotFound -> {
                "The following references point to cache types that do not exist ($count found)"
            }
        }

    private fun <T> TypeReferenceResult.StatusUpdate.toMessage(value: T): String =
        when (this) {
            TypeReferenceResult.CacheTypeNotFound -> {
                "Reference: $value"
            }
        }

    private fun List<TypeReferenceResult.Issue<*>>.toIssueError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toIssueMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeReferenceResult.Issue<*>>.toIssueMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeReferenceResult.StatusIssue.toHeader(count: Int): String =
        when (this) {
            is TypeReferenceResult.CacheTypeHashMismatch -> {
                "The following reference hashes do not match their cache-computed " +
                    "hash ($count found)"
            }
        }

    private fun <T> TypeReferenceResult.StatusIssue.toMessage(value: T): String =
        when (this) {
            is TypeReferenceResult.CacheTypeHashMismatch -> {
                "Invalid hash: $hash\t| Cache hash: $cacheHash\t| Reference: $value"
            }
        }

    private fun List<TypeReferenceResult.Error<*>>.toError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toErrMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeReferenceResult.Error<*>>.toErrMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeReferenceResult.StatusErr.toHeader(count: Int): String =
        when (this) {
            is TypeReferenceResult.KeyTypeMismatch -> {
                "The following references have an unexpected generic key type ($count found)"
            }
            is TypeReferenceResult.KeyValTypeMismatch -> {
                "The following references have an unexpected generic key, value " +
                    "type pair ($count found)"
            }
            is TypeReferenceResult.NameNotFound -> {
                "The following references use names that are not defined in a names.sym " +
                    "file ($count found)"
            }
            is TypeReferenceResult.InvalidImplicitName -> {
                "The following references use names that are not defined in a names.sym " +
                    "file ($count found)"
            }
            is TypeReferenceResult.ImplicitNameNotFound -> {
                "The following references use names that are not defined in a names.sym " +
                    "file ($count found)"
            }
            is TypeReferenceResult.ValTypeMismatch -> {
                "The following references have an unexpected generic value type ($count found)"
            }
        }

    private fun <T> TypeReferenceResult.StatusErr.toMessage(value: T): String =
        when (this) {
            is TypeReferenceResult.KeyTypeMismatch -> {
                "Expected: ${expected?.simpleName}\t| " +
                    "Actual: ${actual?.simpleName}\t| " +
                    "Reference: $value"
            }
            is TypeReferenceResult.KeyValTypeMismatch -> {
                "Expected: ${expectedKey?.simpleName}, ${expectedVal?.simpleName}\t| " +
                    "Actual: ${actualKey?.simpleName}, ${actualVal?.simpleName}\t| " +
                    "Reference: $value"
            }
            is TypeReferenceResult.NameNotFound -> "Hash: $hash\t| Name: \"$name\""
            is TypeReferenceResult.InvalidImplicitName -> "Name not defined"
            is TypeReferenceResult.ImplicitNameNotFound -> "Name: \"$name\""
            is TypeReferenceResult.ValTypeMismatch -> {
                "Expected: ${expected?.simpleName}\t| " +
                    "Actual: ${actual?.simpleName}\t| " +
                    "Reference: $value"
            }
        }

    private companion object {
        private const val MAX_ERROR_LINES: Int = 50
    }
}
