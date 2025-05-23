package org.rsmod.api.type.builders.resolver

public sealed class TypeBuilderResult {
    public data class Success<T>(val value: T, val status: StatusOk) : TypeBuilderResult()

    public data class Update<T>(val value: T, val status: StatusUpdate) : TypeBuilderResult()

    public data class Error<T>(val value: T, val status: StatusErr) : TypeBuilderResult()

    public sealed class StatusOk

    public sealed class StatusUpdate

    public sealed class StatusErr

    public data class NameNotFound(val name: String?) : StatusErr()

    public data class DbTableColumnMismatch(val expected: String?, val actual: List<String?>) :
        StatusErr()

    public object CachePackRequired : StatusUpdate()

    public object FullSuccess : StatusOk()
}

internal fun <T> T.ok(status: TypeBuilderResult.StatusOk): TypeBuilderResult =
    TypeBuilderResult.Success(this, status)

internal fun <T> T.update(status: TypeBuilderResult.StatusUpdate): TypeBuilderResult =
    TypeBuilderResult.Update(this, status)

internal fun <T> T.err(status: TypeBuilderResult.StatusErr): TypeBuilderResult =
    TypeBuilderResult.Error(this, status)
