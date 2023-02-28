package org.rsmod.plugins.types

public class NamedTypeMapHolder {

    public val interfaces: MutableMap<String, NamedInterface> = mutableMapOf()
    public val components: MutableMap<String, NamedComponent> = mutableMapOf()
    public val items: MutableMap<String, NamedItem> = mutableMapOf()
    public val npcs: MutableMap<String, NamedNpc> = mutableMapOf()
    public val objs: MutableMap<String, NamedObject> = mutableMapOf()
    public val anims: MutableMap<String, NamedAnimation> = mutableMapOf()
    public val graphics: MutableMap<String, NamedGraphic> = mutableMapOf()
    public val enums: MutableMap<String, NamedEnum<Any, Any>> = mutableMapOf()
    public val structs: MutableMap<String, NamedStruct> = mutableMapOf()
    public val parameters: MutableMap<String, NamedParameter<Any>> = mutableMapOf()
    public val inventories: MutableMap<String, NamedInventory> = mutableMapOf()
    public val varps: MutableMap<String, NamedVarp> = mutableMapOf()
    public val varbits: MutableMap<String, NamedVarbit> = mutableMapOf()

    public operator fun plusAssign(rhs: NamedTypeMapHolder) {
        interfaces += rhs.interfaces
        components += rhs.components
        items += rhs.items
        npcs += rhs.npcs
        objs += rhs.objs
        anims += rhs.anims
        graphics += rhs.graphics
        enums += rhs.enums
        structs += rhs.structs
        parameters += rhs.parameters
        inventories += rhs.inventories
        varps += rhs.varps
        varbits += rhs.varbits
    }

    public operator fun plus(rhs: NamedTypeMapHolder): NamedTypeMapHolder {
        val lhs = this
        return NamedTypeMapHolder().apply {
            this += lhs
            this += rhs
        }
    }
}
