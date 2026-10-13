package org.runerealm.api.core.module

import com.google.inject.multibindings.Multibinder
import org.runerealm.api.stats.levelmod.InvisibleLevelMod
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.api.stats.xpmod.WornXpModifiers
import org.runerealm.api.stats.xpmod.XpMod
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.module.ExtendedModule

public object StatModModule : ExtendedModule() {
    override fun bind() {
        Multibinder.newSetBinder(binder(), InvisibleLevelMod::class.java)
        bindInstance<InvisibleLevels>()

        addSetBinding<XpMod>(WornXpModifiers::class.java)
        bindInstance<XpModifiers>()
    }
}
