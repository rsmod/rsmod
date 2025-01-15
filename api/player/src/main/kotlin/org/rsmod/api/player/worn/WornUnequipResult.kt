package org.rsmod.api.player.worn

public sealed class WornUnequipResult {
    public data object Success : WornUnequipResult()

    public sealed class Fail(public val message: String?) : WornUnequipResult() {
        public data object InvalidObj : Fail(null)

        public class NotEnoughInvSpace(message: String) : Fail(message)
    }
}
