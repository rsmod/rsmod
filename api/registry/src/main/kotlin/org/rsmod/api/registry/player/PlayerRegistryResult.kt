package org.rsmod.api.registry.player

import kotlin.contracts.contract
import org.rsmod.game.entity.Player

public fun PlayerRegistryResult.Add.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is PlayerRegistryResult.Add.Success) }
    return this is PlayerRegistryResult.Add.Success
}

public fun PlayerRegistryResult.Delete.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is PlayerRegistryResult.Delete.Success) }
    return this is PlayerRegistryResult.Delete.Success
}

public class PlayerRegistryResult {
    public sealed class Add {
        public data object Success : Add()

        public sealed class Failure : Add()

        public data object NoAvailableSlot : Failure()

        public data class ListSlotMismatch(val occupiedBy: Player?) : Failure()
    }

    public sealed class Delete {
        public data object Success : Delete()

        public sealed class Failure : Delete()

        public data object UnexpectedSlot : Failure()

        public data class ListSlotMismatch(val occupiedBy: Player?) : Delete()
    }
}
