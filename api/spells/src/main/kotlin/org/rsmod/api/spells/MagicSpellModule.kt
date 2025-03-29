package org.rsmod.api.spells

import com.google.inject.Provider
import jakarta.inject.Inject
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.module.PluginModule

public class MagicSpellModule : PluginModule() {
    override fun bind() {
        bindProvider(RepositoryProvider::class.java)
    }

    // Since other plugin scripts rely on `MagicSpellRegistry` always being populated with the
    // correct data, we need to ensure its instance calls `init` before any other script startup.
    // To do this, we manually construct the instance and call `init` before handing it off for
    // injection.
    private class RepositoryProvider
    @Inject
    constructor(private val objTypes: ObjTypeList, private val enumResolver: EnumTypeMapResolver) :
        Provider<MagicSpellRegistry> {
        override fun get(): MagicSpellRegistry {
            val registry = MagicSpellRegistry(objTypes, enumResolver)
            registry.init()
            return registry
        }
    }
}
