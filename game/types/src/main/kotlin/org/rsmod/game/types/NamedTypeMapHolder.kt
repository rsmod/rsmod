package org.rsmod.game.types

public class NamedTypeMapHolder {

    public val interfaces: MutableMap<String, NamedInterface> = mutableMapOf()
    public val components: MutableMap<String, NamedComponent> = mutableMapOf()
    public val items: MutableMap<String, NamedItem> = mutableMapOf()
    public val npcs: MutableMap<String, NamedNpc> = mutableMapOf()
    public val objs: MutableMap<String, NamedObject> = mutableMapOf()
    public val anims: MutableMap<String, NamedAnimation> = mutableMapOf()
    public val graphics: MutableMap<String, NamedGraphic> = mutableMapOf()
    public val enums: MutableMap<String, NamedEnum> = mutableMapOf()
    public val structs: MutableMap<String, NamedStruct> = mutableMapOf()
    public val parameters: MutableMap<String, NamedParameter> = mutableMapOf()
    public val inventories: MutableMap<String, NamedInventory> = mutableMapOf()

    // TODO: handle name key collision

    public operator fun set(name: String, value: NamedComponent) {
        components[name] = value
    }

    public operator fun set(name: String, value: NamedInterface) {
        interfaces[name] = value
    }

    public operator fun set(name: String, value: NamedItem) {
        items[name] = value
    }

    public operator fun set(name: String, value: NamedNpc) {
        npcs[name] = value
    }

    public operator fun set(name: String, value: NamedObject) {
        objs[name] = value
    }

    public operator fun set(name: String, value: NamedAnimation) {
        anims[name] = value
    }

    public operator fun set(name: String, value: NamedGraphic) {
        graphics[name] = value
    }

    public operator fun set(name: String, value: NamedEnum) {
        enums[name] = value
    }

    public operator fun set(name: String, value: NamedStruct) {
        structs[name] = value
    }

    public operator fun set(name: String, value: NamedParameter) {
        parameters[name] = value
    }

    public operator fun set(name: String, value: NamedInventory) {
        inventories[name] = value
    }

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
    }

    public operator fun plus(rhs: NamedTypeMapHolder): NamedTypeMapHolder {
        val lhs = this
        return NamedTypeMapHolder().apply {
            this += lhs
            this += rhs
        }
    }
}
