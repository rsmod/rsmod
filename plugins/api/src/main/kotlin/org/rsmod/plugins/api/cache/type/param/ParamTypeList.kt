package org.rsmod.plugins.api.cache.type.param

public class ParamTypeList(private val elements: Map<Int, ParamType>) : Map<Int, ParamType> by elements
