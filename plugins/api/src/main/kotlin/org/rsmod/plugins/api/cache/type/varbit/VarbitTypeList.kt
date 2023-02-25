package org.rsmod.plugins.api.cache.type.varbit

public class VarbitTypeList(private val elements: Map<Int, VarbitType>) : Map<Int, VarbitType> by elements
