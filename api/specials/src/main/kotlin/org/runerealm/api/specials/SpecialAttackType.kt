package org.runerealm.api.specials

import org.runerealm.api.utils.vars.VarEnumDelegate

public enum class SpecialAttackType(override val varValue: Int) : VarEnumDelegate {
    None(0),
    Weapon(1),
    Shield(2),
}
