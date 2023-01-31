package org.rsmod.game.types

public class NamedTypeMapHolder {

    public val interfaces: MutableMap<String, NamedInterface> = mutableMapOf()
    public val components: MutableMap<String, NamedComponent> = mutableMapOf()
    public val items: MutableMap<String, NamedItem> = mutableMapOf()
    public val npcs: MutableMap<String, NamedNpc> = mutableMapOf()
    public val objs: MutableMap<String, NamedObject> = mutableMapOf()

    // TODO: handle name key collision

    public operator fun set(name: String, component: NamedComponent) {
        components[name] = component
    }

    public operator fun set(name: String, uinterface: NamedInterface) {
        interfaces[name] = uinterface
    }

    public operator fun set(name: String, item: NamedItem) {
        items[name] = item
    }

    public operator fun set(name: String, npc: NamedNpc) {
        npcs[name] = npc
    }

    public operator fun set(name: String, obj: NamedObject) {
        objs[name] = obj
    }
}
