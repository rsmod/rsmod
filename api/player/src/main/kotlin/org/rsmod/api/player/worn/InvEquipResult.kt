package org.rsmod.api.player.worn

import org.rsmod.game.type.obj.Wearpos

public sealed class InvEquipResult {
    public data class Success(
        public val unequipWearpos: List<Wearpos>,
        public val equipWearpos: Wearpos,
    ) : InvEquipResult()

    public sealed class Fail(public val messages: List<String>) : InvEquipResult() {
        public data object InvalidObj : Fail(emptyList())

        public class NotEnoughInvSpace(message: String) : Fail(listOf(message))

        public class NotEnoughWornSpace(message: String) : Fail(listOf(message))

        public class StatRequirements(messages: List<String>) : Fail(messages) {
            public constructor(
                messages: Pair<String, String>
            ) : this(listOf(messages.first, messages.second))
        }
    }
}
