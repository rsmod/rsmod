package org.runerealm.api.type.builders.param

import org.runerealm.api.type.builders.TypeBuilder
import org.runerealm.api.type.script.dsl.TypedParamPluginBuilder
import org.runerealm.game.type.param.UnpackedParamType

public abstract class ParamBuilder :
    TypeBuilder<TypedParamPluginBuilder<Any>, UnpackedParamType<*>>() {
    public inline fun <reified T : Any> build(
        internal: String,
        noinline init: TypedParamPluginBuilder<T>.() -> Unit = {},
    ) {
        val type = TypedParamPluginBuilder(T::class, internal).apply(init).build(id = -1)
        cache += type
    }
}
