package org.rsmod.plugins.api.cache.type.npc

public class NpcTypeList(private val elements: Map<Int, NpcType>) : Map<Int, NpcType> by elements
