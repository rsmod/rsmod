package org.rsmod.api.registry.player

import org.rsmod.game.entity.Player

public sealed class PlayerRegistryResult {
    public sealed class Add : PlayerRegistryResult() {
        public val isSuccess: Boolean
            get() = this is AddSuccess

        public val isFailure: Boolean
            get() = !isSuccess
    }

    public data object AddSuccess : Add()

    public sealed class AddError : Add()

    public data object AddErrorInvalidSlot : AddError()

    public data class AddErrorSlotInUse(val occupiedBy: Player) : AddError()

    public sealed class Delete : PlayerRegistryResult() {
        public val isSuccess: Boolean
            get() = this is DeleteSuccess

        public val isFailure: Boolean
            get() = !isSuccess
    }

    public data object DeleteSuccess : Delete()

    public sealed class DeleteError : Delete()

    public data object DeleteErrorInvalidSlot : DeleteError()

    public data class DeleteErrorSlotMismatch(val occupiedBy: Player?) : DeleteError()
}
