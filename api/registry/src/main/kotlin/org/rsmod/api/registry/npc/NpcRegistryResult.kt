package org.rsmod.api.registry.npc

import kotlin.contracts.contract
import org.rsmod.game.entity.Npc

public fun NpcRegistryResult.Add.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is NpcRegistryResult.Add.Success) }
    return this is NpcRegistryResult.Add.Success
}

public fun NpcRegistryResult.Delete.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is NpcRegistryResult.Delete.Success) }
    return this is NpcRegistryResult.Delete.Success
}

public class NpcRegistryResult {
    public sealed class Add {
        public data object Success : Add()

        public sealed class Failure : Add()

        public data object NoAvailableSlot : Failure()
    }

    public sealed class Delete {
        public data object Success : Delete()

        public sealed class Failure : Delete()

        public data object UnexpectedSlot : Failure()

        public data class ListSlotMismatch(val occupiedBy: Npc?) : Failure()
    }
}
