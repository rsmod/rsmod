package org.rsmod.plugins.api.cache.type.item

public class ItemTypeList(private val elements: Map<Int, ItemType>) : Map<Int, ItemType> by elements
