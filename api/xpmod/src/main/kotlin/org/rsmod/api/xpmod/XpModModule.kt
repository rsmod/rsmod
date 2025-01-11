package org.rsmod.api.xpmod

import com.google.inject.multibindings.Multibinder
import org.rsmod.module.ExtendedModule

object XpModModule : ExtendedModule() {
    override fun bind() {
        Multibinder.newSetBinder(binder(), XpMod::class.java)
        bindInstance<XpModifiers>()
    }
}
