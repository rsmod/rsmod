package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.mod.ModLevelType
import org.rsmod.game.type.mod.ModLevelTypeBuilder
import org.rsmod.game.type.mod.UnpackedModLevelType

@DslMarker private annotation class ModLevelBuilderDsl

@ModLevelBuilderDsl
public class ModLevelPluginBuilder(private var internal: String? = null) {
    private val backing: ModLevelTypeBuilder = ModLevelTypeBuilder()

    public var clientCode: Int? by backing::clientCode
    private var accessFlags: Long? by backing::accessFlags

    public fun build(id: Int): UnpackedModLevelType {
        backing.internal = internal
        return backing.build(id)
    }

    public fun permissions(init: PermissionBuilder.() -> Unit) {
        check(backing.accessFlags == null) { "`permissions` already set." }
        val builder = PermissionBuilder().apply(init)
        accessFlags = builder.accessFlags
    }

    @ModLevelBuilderDsl
    public class PermissionBuilder {
        internal var accessFlags: Long = 0L

        public operator fun plusAssign(other: ModLevelType) {
            add(other)
        }

        private fun add(other: ModLevelType) {
            require(other.id in 0..<Long.SIZE_BITS) {
                "ModLevel `id` must be in range [0..63]. ($other)"
            }
            accessFlags = accessFlags or (1L shl other.id)
        }
    }
}
