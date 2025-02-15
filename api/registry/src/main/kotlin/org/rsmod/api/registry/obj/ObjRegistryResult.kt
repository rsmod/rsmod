package org.rsmod.api.registry.obj

import kotlin.contracts.contract
import org.rsmod.game.obj.Obj

public fun ObjRegistryResult.Add.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is ObjRegistryResult.Add.Success) }
    return this is ObjRegistryResult.Add.Success
}

public fun ObjRegistryResult.Delete.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is ObjRegistryResult.Delete.Success) }
    return this is ObjRegistryResult.Delete.Success
}

public class ObjRegistryResult {
    public sealed class Add {
        public sealed class Success : Add()

        public class Merge(public val merged: Obj) : Success()

        public class Split(public val split: List<Obj>) : Success()

        public data object Stack : Success()

        public sealed class Failure : Add()

        public data class BulkNonStackableLimitExceeded(val requestedCount: Int) : Failure()

        public data object InvalidDummyitem : Failure()
    }

    public sealed class Delete {
        public data object Success : Delete()

        public sealed class Failure : Delete()

        public data object InvalidZone : Failure()

        public data object NotRegisteredInZone : Failure()
    }
}
