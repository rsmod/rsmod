package org.rsmod.plugins.types.gen

import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedInterface
import org.rsmod.game.types.NamedItem
import org.rsmod.game.types.NamedNpc
import org.rsmod.game.types.NamedObject
import org.rsmod.game.types.NamedTypeMapHolder
import org.rsmod.game.types.TypeName
import org.rsmod.types.Components
import org.rsmod.types.Interfaces
import org.rsmod.types.Items
import org.rsmod.types.Npcs
import org.rsmod.types.Objs

public class PluginConstTypeNameLoader {

    public fun load(packageName: String): NamedTypeMapHolder {
        val names = NamedTypeMapHolder()
        GENERATED_CONSTANT_FILE.forEach { constantFile ->
            val reflections = Reflections(
                "$packageName.${constantFile.javaClass.simpleName}",
                Scanners.MethodsAnnotated
            )
            reflections.loadAndPut(names, constantFile)
        }
        return names
    }

    private fun Reflections.loadAndPut(names: NamedTypeMapHolder, constantFile: Any) {
        val methods = getMethodsAnnotatedWith(TypeName::class.java)
        methods.forEach { method ->
            /*
             * Every property in these name files must have a receiver
             * (i.e `Items.custom_item`)
             */
            val paramType = method.parameterTypes.first()
            // TODO: can map the methods to constant file - right now we are looping exponentially
            if (paramType != constantFile::class.java) return@forEach
            val property = method.name.camelToSnakeCase().replaceFirst("get_", "")
            val value = method.invoke(paramType, constantFile) as Int
            when (paramType) {
                Interfaces::class.java -> names[property] = NamedInterface(value)
                Components::class.java -> names[property] = NamedComponent(value)
                Items::class.java -> names[property] = NamedItem(value)
                Npcs::class.java -> names[property] = NamedNpc(value)
                Objs::class.java -> names[property] = NamedObject(value)
            }
        }
    }

    private companion object {

        private val GENERATED_CONSTANT_FILE = listOf(
            Interfaces,
            Components,
            Items,
            Npcs,
            Objs
        )

        private val CAMEL_CASE_REGEX = "(?<=[a-zA-Z])[A-Z]".toRegex()

        private fun String.camelToSnakeCase(): String {
            return CAMEL_CASE_REGEX.replace(this) { "_${it.value}" }.lowercase()
        }
    }
}
