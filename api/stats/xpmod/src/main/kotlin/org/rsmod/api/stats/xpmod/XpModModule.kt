package org.rsmod.api.stats.xpmod

import org.rsmod.module.ExtendedModule

object XpModModule : ExtendedModule() {
    override fun bind() {
        addSetBinding<XpMod>(WornXpModifiers::class.java)
        bindInstance<XpModifiers>()
    }
}
