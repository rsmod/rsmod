package org.rsmod.api.type.builders.param

import kotlin.reflect.KClass
import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.ParamPluginBuilder
import org.rsmod.api.type.script.dsl.TypedParamPluginBuilder
import org.rsmod.game.type.param.UnpackedParamType

public abstract class ParamBuilder : HashTypeBuilder<ParamPluginBuilder, UnpackedParamType<*>>() {
    override fun build(internal: String, init: ParamPluginBuilder.() -> Unit) {
        val type = ParamPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }

    public inline fun <reified T : Any> buildTyped(
        internal: String,
        noinline init: TypedParamPluginBuilder<T>.() -> Unit = {},
    ): Unit = build(T::class, internal, init)

    public fun <T : Any> build(
        type: KClass<T>,
        internal: String,
        init: TypedParamPluginBuilder<T>.() -> Unit = {},
    ) {
        val type = TypedParamPluginBuilder(type, internal).apply(init).build(id = -1)
        cache += type
    }
}
