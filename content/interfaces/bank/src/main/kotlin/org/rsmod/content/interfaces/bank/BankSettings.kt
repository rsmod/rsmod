package org.rsmod.content.interfaces.bank

import org.rsmod.api.utils.vars.VarEnumDelegate

enum class QuantityMode(override val varValue: Int) : VarEnumDelegate {
    One(0),
    Five(1),
    Ten(2),
    X(3),
    All(4),
}

enum class TabDisplayMode(override val varValue: Int) : VarEnumDelegate {
    Obj(0),
    Digit(1),
    Roman(2),
}

enum class BankFillerMode(override val varValue: Int) : VarEnumDelegate {
    All(0),
    One(1),
    Ten(2),
    Fifty(3),
    X(4),
}
