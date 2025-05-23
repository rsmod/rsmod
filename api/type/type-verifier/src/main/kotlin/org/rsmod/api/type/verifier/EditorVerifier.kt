package org.rsmod.api.type.verifier

import jakarta.inject.Inject
import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.api.type.editors.resolver.TypeEditorResult
import org.rsmod.api.type.verifier.TypeVerifier.ResolutionError
import org.rsmod.api.type.verifier.TypeVerifier.Verification

public class EditorVerifier @Inject constructor(private val editors: TypeEditorResolverMap) {
    public fun verifyAll(): Verification {
        val symbols = editors.symbolErrors
        if (symbols.isNotEmpty()) {
            val error = symbols.toError()
            return Verification.MissingSymbol(error)
        }

        val updates = editors.updates
        if (updates.isNotEmpty()) {
            val error = updates.toUpdateError()
            return Verification.CacheUpdateRequired(error)
        }

        val errors = editors.errors
        if (errors.isNotEmpty()) {
            val error = errors.toError()
            return Verification.Failure(error)
        }

        return Verification.FullSuccess
    }

    private fun List<TypeEditorResult.Update<*>>.toUpdateError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toUpdateMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeEditorResult.Update<*>>.toUpdateMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeEditorResult.StatusUpdate.toHeader(count: Int): String =
        when (this) {
            TypeEditorResult.CachePackRequired -> {
                "The following cache type edits need to be packed ($count found)"
            }
        }

    private fun <T> TypeEditorResult.StatusUpdate.toMessage(value: T): String =
        when (this) {
            TypeEditorResult.CachePackRequired -> "Edit: $value"
        }

    private fun List<TypeEditorResult.Error<*>>.toError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toErrMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeEditorResult.Error<*>>.toErrMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeEditorResult.StatusErr.toHeader(count: Int): String =
        when (this) {
            is TypeEditorResult.NameNotFound -> {
                "The following cache edits use names that are not defined in a .sym " +
                    "file ($count found)"
            }
            TypeEditorResult.CacheTypeDoesNotExit -> {
                "The following cache edits are trying to modify a type not found " +
                    "in cache ($count found)"
            }
            is TypeEditorResult.DbTableColumnMismatch -> {
                "The following DbTable cache edits are trying to use a mismatching column " +
                    "($count found)"
            }
        }

    private fun <T> TypeEditorResult.StatusErr.toMessage(value: T): String =
        when (this) {
            is TypeEditorResult.NameNotFound -> "Name: \"$name\"\t| Edit: $value"
            TypeEditorResult.CacheTypeDoesNotExit -> "Edit: $value"
            is TypeEditorResult.DbTableColumnMismatch -> {
                "Expected Table: '$expected'\t| Actual Table(s): $actual"
            }
        }

    private companion object {
        private const val MAX_ERROR_LINES: Int = 50
    }
}
