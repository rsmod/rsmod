package org.rsmod.api.player.input

import org.rsmod.game.entity.player.PlayerUid

public data class ResumePNameDialogInput(val result: Result) {
    public sealed class Result {
        public object NotFound : Result()

        public data class SameWorld(val uid: PlayerUid) : Result()
    }
}
