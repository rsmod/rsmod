package org.rsmod.coroutine.suspension

import kotlin.coroutines.Continuation
import org.rsmod.coroutine.resume.ResumeCondition

public data class GameCoroutineSuspension<T>(
    public val continuation: Continuation<T>,
    public val condition: ResumeCondition<T>,
)
