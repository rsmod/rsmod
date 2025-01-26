package org.rsmod.api.type.script.dsl

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarTypeMap
import org.rsmod.game.type.param.ParamTypeBuilder
import org.rsmod.game.type.param.UnpackedParamType

@DslMarker private annotation class ParamBuilderDsl

@ParamBuilderDsl
public class ParamPluginBuilder(internal: String?) {
    private val backing = TypedParamPluginBuilder<Any>(null, internal)
    public var autoDisable: Boolean? by backing::autoDisable
    public var transmit: Boolean by backing::transmit

    public fun build(id: Int): UnpackedParamType<Any> = backing.build(id)
}

@ParamBuilderDsl
public class TypedParamPluginBuilder<T : Any>(
    public val type: KClass<T>?,
    public var internal: String? = null,
) {
    public var default: T? = null
    public var autoDisable: Boolean? = null
    public var transmit: Boolean = false

    @Suppress("UNCHECKED_CAST")
    public fun build(id: Int): UnpackedParamType<T> {
        val literal = type?.let { CacheVarTypeMap.classedLiterals[it] }
        if (type != null && literal == null) {
            throw NotImplementedError(
                "`${type.simpleName}` types are not defined in `CacheVarTypeMap`."
            )
        }
        val backing = ParamTypeBuilder<T>(type, internal)
        val default = default
        if (default != null && literal != null) {
            val codec = CacheVarTypeMap.findCodec<Any, Any>(type)
            val primitive = codec.encode(default)
            if (primitive is Int) {
                backing.defaultInt = primitive
            } else if (primitive is String) {
                backing.defaultStr = primitive
            }
        }
        backing.typeCharId = literal?.char
        backing.autoDisable = autoDisable
        backing.typedDefault = default
        backing.transmit = transmit
        return backing.build(id)
    }
}
