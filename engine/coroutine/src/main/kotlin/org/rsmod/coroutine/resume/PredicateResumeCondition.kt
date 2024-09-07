package org.rsmod.coroutine.resume

public class PredicateResumeCondition(public val resume: () -> Boolean) : ResumeCondition<Unit> {
    override fun resume(): Boolean = resume.invoke()

    override fun value(): Unit = Unit
}
