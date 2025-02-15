package org.rsmod.api.registry.loc

import kotlin.contracts.contract

public fun LocRegistryResult.Add.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is LocRegistryResult.Add.Success) }
    return this is LocRegistryResult.Add.Success
}

public fun LocRegistryResult.Delete.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is LocRegistryResult.Delete.Success) }
    return this is LocRegistryResult.Delete.Success
}

public class LocRegistryResult {
    public sealed class Add {
        public sealed class Success : Add()

        public data object SpawnedMapLoc : Success()

        public data object SpawnedDynamic : Success()
    }

    public sealed class Delete {
        public sealed class Success : Delete()

        public data object RemovedMapLoc : Success()

        public data object RemovedDynamic : Success()

        public sealed class Failure : Delete()

        public data object LocNotFound : Failure()
    }
}
