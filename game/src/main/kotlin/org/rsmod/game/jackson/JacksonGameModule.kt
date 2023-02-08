package org.rsmod.game.jackson

import com.fasterxml.jackson.databind.Module
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder

public object JacksonGameModule : AbstractModule() {

    override fun configure() {
        Multibinder.newSetBinder(binder(), Module::class.java)
            .addBinding().to(JacksonSimpleGameModule::class.java)
    }
}
