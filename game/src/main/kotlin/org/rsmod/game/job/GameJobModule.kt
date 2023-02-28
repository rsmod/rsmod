package org.rsmod.game.job

import com.google.inject.AbstractModule
import org.rsmod.game.job.boot.GameBootTaskModule

public object GameJobModule : AbstractModule() {

    override fun configure() {
        install(GameBootTaskModule)
    }
}
