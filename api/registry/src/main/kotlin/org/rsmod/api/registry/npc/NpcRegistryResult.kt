package org.rsmod.api.registry.npc

import org.rsmod.game.entity.Npc

public sealed class NpcRegistryResult {
    public sealed class Add : NpcRegistryResult() {
        public val isSuccess: Boolean
            get() = this is AddSuccess

        public val isFailure: Boolean
            get() = !isSuccess
    }

    public data object AddSuccess : Add()

    public sealed class AddError : Add()

    public data object AddErrorInvalidSlot : AddError()

    public sealed class Delete : NpcRegistryResult() {
        public val isSuccess: Boolean
            get() = this is DeleteSuccess

        public val isFailure: Boolean
            get() = !isSuccess
    }

    public data object DeleteSuccess : Delete()

    public sealed class DeleteError : Delete()

    public data object DeleteErrorInvalidSlot : DeleteError()

    public data class DeleteErrorSlotMismatch(val occupiedBy: Npc?) : DeleteError()
}
