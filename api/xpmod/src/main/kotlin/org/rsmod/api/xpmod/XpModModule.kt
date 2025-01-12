package org.rsmod.api.xpmod

import org.rsmod.module.ExtendedModule

object XpModModule : ExtendedModule() {
    override fun bind() {
        addSetBinding<XpMod>(WornXpModifiers::class.java)
        bindInstance<XpModifiers>()
    }
}
