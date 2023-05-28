package org.rsmod.game.model.vars

public class VariableMap(
    private val variables: MutableMap<Int, Int> = mutableMapOf()
) : MutableMap<Int, Int> by variables
