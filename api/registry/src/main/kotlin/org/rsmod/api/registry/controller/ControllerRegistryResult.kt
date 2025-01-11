package org.rsmod.api.registry.controller

import org.rsmod.game.entity.Controller

public sealed class ControllerRegistryResult {
    public sealed class Add : ControllerRegistryResult() {
        public val isSuccess: Boolean
            get() = this is AddSuccess

        public val isFailure: Boolean
            get() = !isSuccess
    }

    public data object AddSuccess : Add()

    public sealed class AddError : Add()

    public data object AddErrorInvalidSlot : AddError()

    public sealed class Delete : ControllerRegistryResult() {
        public val isSuccess: Boolean
            get() = this is DeleteSuccess

        public val isFailure: Boolean
            get() = !isSuccess
    }

    public data object DeleteSuccess : Delete()

    public sealed class DeleteError : Delete()

    public data object DeleteErrorInvalidSlot : DeleteError()

    public data class DeleteErrorSlotMismatch(val occupiedBy: Controller?) : DeleteError()
}
