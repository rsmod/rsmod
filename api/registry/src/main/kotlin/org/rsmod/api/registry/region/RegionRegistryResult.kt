package org.rsmod.api.registry.region

import org.rsmod.game.region.Region

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
