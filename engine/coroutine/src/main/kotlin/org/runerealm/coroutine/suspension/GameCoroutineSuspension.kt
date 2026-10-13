package org.runerealm.coroutine.suspension

import kotlin.coroutines.Continuation
import org.runerealm.coroutine.resume.ResumeCondition

public data class GameCoroutineSuspension<T>(
    public val continuation: Continuation<T>,
    public val condition: ResumeCondition<T>,
)
