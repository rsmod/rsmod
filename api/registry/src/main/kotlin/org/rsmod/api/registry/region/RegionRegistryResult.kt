package org.rsmod.api.registry.region

import kotlin.contracts.contract
import org.rsmod.game.region.Region

public fun RegionRegistryResult.Add.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is RegionRegistryResult.Add.Success) }
    return this is RegionRegistryResult.Add.Success
}

public fun RegionRegistryResult.Delete.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is RegionRegistryResult.Delete.Success) }
    return this is RegionRegistryResult.Delete.Success
}

public class RegionRegistryResult {
    public sealed class Add {
        public sealed class Success(public val region: Region) : Add()

        public class CreateSmall(region: Region) : Success(region)

        public class CreateLarge(region: Region) : Success(region)

        public sealed class Failure : Add()

        public data object NoAvailableSlot : Failure()
    }

    public sealed class Delete {
        public sealed class Success : Delete()

        public data object RemoveSmall : Success()

        public data object RemoveLarge : Success()

        public sealed class Failure : Delete()

        public data object UnexpectedSlot : Failure()

        public data class ListSlotMismatch(val occupiedBy: Region?) : Failure()
    }
}
