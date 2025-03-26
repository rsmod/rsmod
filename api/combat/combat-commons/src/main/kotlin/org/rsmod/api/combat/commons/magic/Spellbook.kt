package org.rsmod.api.combat.commons.magic

import org.rsmod.api.utils.vars.VarEnumDelegate

public enum class Spellbook(override val varValue: Int) : VarEnumDelegate {
    Standard(0),
    Ancients(1),
    Lunars(2),
    Arceuus(3);

    public companion object {
        public operator fun get(id: Int): Spellbook? =
            when (id) {
                Standard.varValue -> Standard
                Ancients.varValue -> Ancients
                Lunars.varValue -> Lunars
                Arceuus.varValue -> Arceuus
                else -> null
            }
    }
}
