package org.rsmod.plugins.api.model.ui.gameframe

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class GameframeModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<GameframeList>().`in`(scope)
    }
}
