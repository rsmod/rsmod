package org.rsmod.api.type.editors.resolver

public sealed class TypeEditorResult {
    public data class Success<T>(val value: T, val status: StatusOk) : TypeEditorResult()

    public data class Update<T>(val value: T, val status: StatusUpdate) : TypeEditorResult()

    public data class Error<T>(val value: T, val status: StatusErr) : TypeEditorResult()

    public sealed class StatusOk

    public sealed class StatusUpdate

    public sealed class StatusErr

    public data class NameNotFound(val name: String?) : StatusErr()

    public data class DbTableColumnMismatch(val expected: String?, val actual: List<String?>) :
        StatusErr()

    public object CacheTypeDoesNotExit : StatusErr()

    public object CachePackRequired : StatusUpdate()

    public object FullSuccess : StatusOk()
}

internal fun <T> T.ok(status: TypeEditorResult.StatusOk): TypeEditorResult =
    TypeEditorResult.Success(this, status)

internal fun <T> T.update(status: TypeEditorResult.StatusUpdate): TypeEditorResult =
    TypeEditorResult.Update(this, status)

internal fun <T> T.err(status: TypeEditorResult.StatusErr): TypeEditorResult =
    TypeEditorResult.Error(this, status)
