package org.rsmod.game.coroutines.resume

public class TickResumeCondition(private var ticks: Int) : ResumeCondition<Unit> {

    override fun resumeOrNull(): Unit? {
        if (--ticks == 0) return Unit
        return null
    }
}
