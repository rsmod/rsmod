package org.rsmod.api.registry.obj

import org.rsmod.game.obj.Obj

public sealed class ObjRegistryResult {
    public val isSuccess: Boolean
        get() = this is Success

    public sealed class Fail : ObjRegistryResult()

    public data class BulkNonStackableLimitExceeded(val requestedCount: Int) : Fail()

    public sealed class Success : ObjRegistryResult()

    public class Merge(public val merged: Obj) : Success()

    public class Split(public val split: List<Obj>) : Success()

    public data object Stack : Success()
}
