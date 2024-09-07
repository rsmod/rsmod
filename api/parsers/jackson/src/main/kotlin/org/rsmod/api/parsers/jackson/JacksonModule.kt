package org.rsmod.api.parsers.jackson

import com.fasterxml.jackson.databind.Module
import com.google.inject.multibindings.Multibinder
import org.rsmod.module.ExtendedModule

public object JacksonModule : ExtendedModule() {
    override fun bind() {
        Multibinder.newSetBinder(binder(), Module::class.java)
            .addBinding()
            .to(JacksonCodecs::class.java)
    }
}
