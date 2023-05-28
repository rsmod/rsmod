package org.rsmod.game.model.clock

import org.rsmod.game.model.vars.VariableMap

public class ClockMap(private val delegate: VariableMap) : MutableMap<Int, Int> by delegate
