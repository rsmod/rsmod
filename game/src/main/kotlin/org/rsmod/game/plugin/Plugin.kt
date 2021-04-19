package org.rsmod.game.plugin

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import org.rsmod.game.action.Action
import org.rsmod.game.action.ActionBus
import org.rsmod.game.action.ActionExecutor
import org.rsmod.game.cmd.CommandMap
import org.rsmod.game.event.Event
import org.rsmod.game.event.EventBus
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.ui.type.ComponentType
import org.rsmod.game.model.ui.type.InterfaceType
import org.rsmod.game.model.vars.type.VarbitType
import org.rsmod.game.model.vars.type.VarpType
import org.rsmod.game.name.NamedTypeMap
import kotlin.properties.ObservableProperty

open class Plugin(
    val injector: Injector,
    val eventBus: EventBus,
    val actionBus: ActionBus,
    val commands: CommandMap
) {

    private val npcNames: NamedTypeMap<NpcType> by inject()

    private val itemNames: NamedTypeMap<ItemType> by inject()

    private val objNames: NamedTypeMap<ObjectType> by inject()

    private val componentNames: NamedTypeMap<ComponentType> by inject()

    private val interfaceNames: NamedTypeMap<InterfaceType> by inject()

    private val varpNames: NamedTypeMap<VarpType> by inject()

    private val varbitNames: NamedTypeMap<VarbitType> by inject()

    inline fun <reified T : Event> onEvent() = eventBus.subscribe<T>()

    inline fun <reified T : Action> onAction(id: Int, noinline executor: ActionExecutor<T>) =
        onAction(id.toLong(), executor)

    inline fun <reified T : Action> onAction(id: Long, noinline executor: ActionExecutor<T>) {
        val registered = actionBus.register(id, executor)
        if (!registered) {
            error("Action with id has already been set (id=$id, type=${T::class.simpleName})")
        }
    }

    inline fun <reified T : Action> onAction(noinline executor: ActionExecutor<T>) {
        actionBus.register(executor)
    }

    inline fun <reified T> inject(): ObservableProperty<T> = InjectedProperty(injector.getInstance())

    fun npc(name: String): NpcType {
        return npcNames[name] ?: error("Npc with name (or alias) \"$name\" not found.")
    }

    fun item(name: String): ItemType {
        return itemNames[name] ?: error("Item with name (or alias) \"$name\" not found.")
    }

    fun obj(name: String): ObjectType {
        return objNames[name] ?: error("Object with name (or alias) \"$name\" not found.")
    }

    fun component(name: String): ComponentType {
        return componentNames[name] ?: error("Component with alias \"$name\" not found.")
    }

    fun inter(name: String): InterfaceType {
        return interfaceNames[name] ?: error("Interface with name \"$name\" not found.")
    }

    fun varp(name: String): VarpType {
        return varpNames[name] ?: error("Varp with name \"$name\" not found.")
    }

    fun varbit(name: String): VarbitType {
        return varbitNames[name] ?: error("Varbit with name \"$name\" not found.")
    }

    fun npcs(vararg names: String): Iterable<NpcType> {
        return names.map { npc(it) }
    }

    fun items(vararg names: String): Iterable<ItemType> {
        return names.map { item(it) }
    }

    fun objs(vararg names: String): Iterable<ObjectType> {
        return names.map { obj(it) }
    }

    class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
}
