package org.rsmod.api.registry.controller

import kotlin.contracts.contract
import org.rsmod.game.entity.Controller

public fun ControllerRegistryResult.Add.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is ControllerRegistryResult.Add.Success) }
    return this is ControllerRegistryResult.Add.Success
}

public fun ControllerRegistryResult.Delete.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is ControllerRegistryResult.Delete.Success) }
    return this is ControllerRegistryResult.Delete.Success
}

public class ControllerRegistryResult {
    public sealed class Add {
        public data object Success : Add()

        public sealed class Failure : Add()

        public data object NoAvailableSlot : Failure()
    }

    public sealed class Delete {
        public data object Success : Delete()

        public sealed class Failure : Delete()

        public data object UnexpectedSlot : Failure()

        public data class ListSlotMismatch(val occupiedBy: Controller?) : Failure()
    }
}
