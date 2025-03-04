package org.rsmod.game.type.inv

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

@DslMarker private annotation class InvBuilderDsl

@InvBuilderDsl
public class InvTypeBuilder(public var internal: String? = null) {
    public var scope: InvScope? = null
    public var stack: InvStackType? = null
    public var size: Int? = null
    public var flags: Int? = null
    public var restock: Boolean? = null
    public var allStock: Boolean? = null
    public var protect: Boolean? = null
    public var runWeight: Boolean? = null
    public var dummyInv: Boolean? = null
    public var placeholders: Boolean? = null
    public var stock: Array<InvStock?>? = null

    public fun build(id: Int): UnpackedInvType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val scope = scope ?: DEFAULT_SCOPE
        val stack = stack ?: DEFAULT_STACK
        val size = size ?: DEFAULT_SIZE
        val flags = flags
        return if (flags == null) {
            val restock = restock == true
            val allStock = allStock == true
            val protect = protect ?: DEFAULT_PROTECT
            val runWeight = runWeight == true
            val dummyInv = dummyInv == true
            val placeholders = placeholders == true
            UnpackedInvType(
                scope = scope,
                stack = stack,
                size = size,
                restock = restock,
                allStock = allStock,
                protect = protect,
                runWeight = runWeight,
                dummyInv = dummyInv,
                placeholders = placeholders,
                stock = stock,
                internalId = id,
                internalName = internal,
            )
        } else {
            UnpackedInvType(
                scope = scope,
                stack = stack,
                size = size,
                flags = flags,
                stock = stock,
                internalId = id,
                internalName = internal,
            )
        }
    }

    public companion object : MergeableCacheBuilder<UnpackedInvType> {
        public const val DEFAULT_SIZE: Int = 1
        public const val DEFAULT_PROTECT: Boolean = true
        public val DEFAULT_SCOPE: InvScope = InvScope.Temp
        public val DEFAULT_STACK: InvStackType = InvStackType.Normal
        public val DEFAULT_FLAGS: Int by lazy { defaultFlags() }

        private fun defaultFlags(): Int =
            UnpackedInvType.pack(
                protect = DEFAULT_PROTECT,
                allStock = false,
                restock = false,
                runWeight = false,
                dummyInv = false,
                placeholders = false,
            )

        override fun merge(edit: UnpackedInvType, base: UnpackedInvType): UnpackedInvType {
            val scope = select(edit, base, DEFAULT_SCOPE) { scope }
            val stack = select(edit, base, DEFAULT_STACK) { stack }
            val size = select(edit, base, DEFAULT_SIZE) { size }
            val flags = select(edit, base, DEFAULT_FLAGS) { flags }
            val stock = select(edit, base, default = null) { stock }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedInvType(
                scope = scope,
                stack = stack,
                size = size,
                flags = flags,
                stock = stock,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
