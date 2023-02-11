package org.rsmod.game.coroutines.resume

public class PredicateResumeCondition(
    public val resume: () -> Boolean
) : ResumeCondition<Boolean> {

    override fun resumeOrNull(): Boolean? {
        if (resume()) return true
        return null
    }
}
