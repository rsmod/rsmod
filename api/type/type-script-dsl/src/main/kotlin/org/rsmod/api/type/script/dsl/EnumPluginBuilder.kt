package org.rsmod.api.type.script.dsl

import kotlin.reflect.KClass
import org.rsmod.game.type.enums.EnumTypeBuilder
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.literal.CacheVarTypeMap

@DslMarker private annotation class EnumBuildersDsl

@EnumBuildersDsl
public class AutoIntEnumPluginBuilder<V : Any>(
    expectedValType: KClass<V>,
    public var internal: String? = null,
) {
    private val backing = EnumPluginBuilder(Int::class, expectedValType)

    private var currKey = 0

    public var default: V? by backing::default

    public fun build(id: Int): UnpackedEnumType<Int, V> {
        backing.internal = internal
        return backing.build(id)
    }

    public fun values(init: ListBuilder.() -> Unit) {
        ListBuilder().apply(init)
    }

    public operator fun plusAssign(value: V?) {
        backing.vals[currKey++] = value
    }

    @EnumBuildersDsl
    public inner class ListBuilder {
        private fun add(value: V?) {
            this@AutoIntEnumPluginBuilder.plusAssign(value)
        }

        public operator fun V.unaryMinus() {
            add(this)
        }
    }
}

@EnumBuildersDsl
public class EnumPluginBuilder<K : Any, V : Any>(
    private val expectedKeyType: KClass<K>,
    private val expectedValType: KClass<V>,
    public var internal: String? = null,
) {
    public var default: V? = null
    public var vals: MutableMap<K, V?> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    public fun build(id: Int): UnpackedEnumType<K, V> {
        val key =
            checkNotNull(CacheVarTypeMap.classedLiterals[expectedKeyType]) {
                "Type for key is not implemented in " +
                    "CacheVarTypeMap.classedLiterals: ${expectedKeyType.simpleName}"
            }
        val value =
            checkNotNull(CacheVarTypeMap.classedLiterals[expectedValType]) {
                "Type for value is not implemented in " +
                    "CacheVarTypeMap.classedLiterals: ${expectedValType.simpleName}"
            }
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val backing = EnumTypeBuilder<K, V>(expectedKeyType, expectedValType, internal)
        backing.keyCharId = key.char
        backing.valCharId = value.char
        backing.typedMap = vals
        return backing.build(id) ?: error("Could not build $id.")
    }

    public fun entries(init: EntryBuilder.() -> Unit) {
        EntryBuilder().apply(init)
    }

    public operator fun set(key: K, value: V?) {
        vals[key] = value
    }

    @EnumBuildersDsl
    public inner class EntryBuilder {
        private operator fun set(key: K, value: V?) {
            this@EnumPluginBuilder[key] = value
        }

        public infix fun K.with(value: V?) {
            set(this, value)
        }

        // We disable this to avoid conflict between stdlib `to` infix, which can cause confusion.
        @Deprecated(
            message = "Use the `with` function instead.",
            replaceWith = ReplaceWith("this with value"),
            level = DeprecationLevel.ERROR,
        )
        @Suppress("UNUSED_PARAMETER")
        public infix fun K.to(value: V?): Nothing {
            throw UnsupportedOperationException("Prefer using the `with` infix function instead.")
        }
    }
}
