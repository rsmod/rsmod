package org.rsmod.game.job.boot

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object GameBootTaskModule : AbstractModule() {

    override fun configure() {
        bind(GameBootTaskScheduler::class.java).`in`(Scopes.SINGLETON)
    }
}
