package org.rsmod.game.coroutines

import org.rsmod.game.coroutines.resume.ResumeCondition
import kotlin.coroutines.Continuation

public data class GameCoroutineSuspension<T>(
    public val continuation: Continuation<T>,
    public val condition: ResumeCondition<T>
)
