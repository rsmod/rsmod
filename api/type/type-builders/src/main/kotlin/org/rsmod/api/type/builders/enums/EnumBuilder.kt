package org.rsmod.api.type.builders.enums

import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.script.dsl.AutoIntEnumPluginBuilder
import org.rsmod.api.type.script.dsl.EnumPluginBuilder
import org.rsmod.game.type.enums.UnpackedEnumType

public abstract class EnumBuilder :
    TypeBuilder<EnumPluginBuilder<Any, Any>, UnpackedEnumType<*, *>>() {
    public inline fun <reified K : Any, reified V : Any> build(
        internal: String,
        noinline init: EnumPluginBuilder<K, V>.() -> Unit,
    ) {
        val type = EnumPluginBuilder(K::class, V::class, internal).apply(init).build(id = -1)
        cache += type
    }

    public inline fun <reified V : Any> buildAutoInt(
        internal: String,
        noinline init: AutoIntEnumPluginBuilder<V>.() -> Unit,
    ) {
        val type = AutoIntEnumPluginBuilder(V::class, internal).apply(init).build(id = -1)
        cache += type
    }
}
