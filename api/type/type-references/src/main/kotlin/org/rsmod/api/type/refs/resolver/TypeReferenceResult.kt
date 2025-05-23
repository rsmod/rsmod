package org.rsmod.api.type.refs.resolver

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarLiteral

public sealed class TypeReferenceResult {
    public data class Success<T>(val value: T, val status: StatusOk) : TypeReferenceResult()

    public data class Update<T>(val value: T, val status: StatusUpdate) : TypeReferenceResult()

    public data class Issue<T>(val value: T, val status: StatusIssue) : TypeReferenceResult()

    public data class Error<T>(val value: T, val status: StatusErr) : TypeReferenceResult()

    public sealed class StatusOk

    public sealed class StatusUpdate

    public sealed class StatusIssue

    public sealed class StatusErr

    public data class NameNotFound(val name: String, val hash: Long?) : StatusErr()

    public data class KeyTypeMismatch(val expected: KClass<*>?, val actual: KClass<*>?) :
        StatusErr()

    public data class ValTypeMismatch(val expected: KClass<*>?, val actual: KClass<*>?) :
        StatusErr()

    public data class KeyValTypeMismatch(
        val expectedKey: KClass<*>?,
        val actualKey: KClass<*>?,
        val expectedVal: KClass<*>?,
        val actualVal: KClass<*>?,
    ) : StatusErr()

    public data class DbColumnTypeMismatch(
        val column: String,
        val index: Int,
        val expected: List<CacheVarLiteral?>,
        val actual: List<CacheVarLiteral>,
    ) : StatusErr()

    public data object InvalidImplicitName : StatusErr()

    public data class ImplicitNameNotFound(val name: String?) : StatusErr()

    public data object CacheTypeNotFound : StatusUpdate()

    public data class CacheTypeHashMismatch(val hash: Long?, val cacheHash: Long) : StatusIssue()

    public data object FullSuccess : StatusOk()
}

internal fun <T> T.ok(status: TypeReferenceResult.StatusOk): TypeReferenceResult =
    TypeReferenceResult.Success(this, status)

internal fun <T> T.update(status: TypeReferenceResult.StatusUpdate): TypeReferenceResult =
    TypeReferenceResult.Update(this, status)

internal fun <T> T.issue(status: TypeReferenceResult.StatusIssue): TypeReferenceResult =
    TypeReferenceResult.Issue(this, status)

internal fun <T> T.err(status: TypeReferenceResult.StatusErr): TypeReferenceResult =
    TypeReferenceResult.Error(this, status)
