package org.rsmod.api.registry.loc

import kotlin.contracts.contract
import org.rsmod.game.region.Region

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

        public data object NormalMapLoc : Success()

        public data object NormalSpawned : Success()

        public sealed class RegionSuccess(public val regionSlot: Int, public val regionUid: Int) :
            Success()

        public class RegionSpawned(region: Region) : RegionSuccess(region.slot, region.uid)

        public sealed class Failure : Add()

        public data object RegionNotRegistered : Failure()

        public data object RegionZoneNotRegistered : Failure()
    }

    public sealed class Delete {
        public sealed class Success : Delete()

        public data object NormalMapLoc : Success()

        public data object NormalSpawned : Success()

        public sealed class RegionSuccess(public val regionSlot: Int, public val regionUid: Int) :
            Success()

        public class RegionMapLoc(region: Region) : RegionSuccess(region.slot, region.uid)

        public class RegionSpawned(region: Region) : RegionSuccess(region.slot, region.uid)

        public sealed class Failure : Delete()

        public data object LocNotFound : Failure()

        public data object RegionNotRegistered : Failure()

        public data object RegionZoneNotRegistered : Failure()
    }
}
