package org.rsmod.plugins.api.cache.type.obj

public class ObjectTypeList(private val elements: Map<Int, ObjectType>) : Map<Int, ObjectType> by elements
