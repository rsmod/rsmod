package org.rsmod.api.core.module

import com.google.inject.multibindings.Multibinder
import org.rsmod.api.stats.levelmod.InvisibleLevelMod
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.stats.xpmod.WornXpModifiers
import org.rsmod.api.stats.xpmod.XpMod
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.module.ExtendedModule

public object StatModModule : ExtendedModule() {
    override fun bind() {
        Multibinder.newSetBinder(binder(), InvisibleLevelMod::class.java)
        bindInstance<InvisibleLevels>()

        addSetBinding<XpMod>(WornXpModifiers::class.java)
        bindInstance<XpModifiers>()
    }
}
