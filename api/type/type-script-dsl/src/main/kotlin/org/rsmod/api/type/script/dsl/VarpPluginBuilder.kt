package org.rsmod.api.type.script.dsl

import org.rsmod.annotations.InternalApi
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpLifetime
import org.rsmod.game.type.varp.VarpTransmitLevel
import org.rsmod.game.type.varp.VarpTypeBuilder

@DslMarker private annotation class VarpBuilderDsl

@VarpBuilderDsl
public class VarpPluginBuilder(@InternalApi public var internal: String? = null) {
    private val backing = VarpTypeBuilder()

    public var clientCode: Int? by backing::clientCode

    private var scope: VarpLifetime? by backing::scope

    // Varps created through this builder are usually server-side only.
    private var transmit: VarpTransmitLevel? = VarpTransmitLevel.Never

    /** If set to `true` this varp will check all associated varbits to detect bit collisions. */
    public var collisionDetection: Boolean = true

    /**
     * Determines whether the varp is transmitted to player clients when changed.
     * - When set to `true`, [transmit] is set to [VarpTransmitLevel.Never], meaning the varp **is
     *   never sent** to the player's client.
     * - When set to `false`, [transmit] is set to [VarpTransmitLevel.OnSetAlways], meaning the varp
     *   **is always sent** to the player's client whenever it changes.
     */
    public var transmitNever: Boolean
        get() = transmit?.never == true
        set(value) {
            val transmit = if (value) VarpTransmitLevel.Never else VarpTransmitLevel.OnSetAlways
            this.transmit = transmit
        }

    /**
     * Determines whether the varp is **always transmitted** to the player's client when changed.
     * - When set to `true`, [transmit] is set to [VarpTransmitLevel.OnSetAlways], meaning the varp
     *   **is always sent** to the player's client whenever it updates.
     * - When set to `false`, [transmit] is set to [VarpTransmitLevel.Never], meaning the varp **is
     *   never sent** to the player's client.
     */
    public var transmitAlways: Boolean
        get() = transmit?.always == true
        set(value) {
            val transmit = if (value) VarpTransmitLevel.OnSetAlways else VarpTransmitLevel.Never
            this.transmit = transmit
        }

    /**
     * Determines whether the varp is transmitted **only when its value changes**.
     * - When set to `true`, [transmit] is set to [VarpTransmitLevel.OnSetDifferent], meaning the
     *   varp **is only sent** to the player's client if the new value differs from the previous
     *   one.
     * - When set to `false`, [transmit] is set to [VarpTransmitLevel.OnSetAlways], meaning the varp
     *   **is always sent** to the player's client whenever it updates, even if the value is the
     *   same.
     */
    public var transmitOnDiff: Boolean
        get() = transmit?.never == true
        set(value) {
            val transmit =
                if (value) VarpTransmitLevel.OnSetDifferent else VarpTransmitLevel.OnSetAlways
            this.transmit = transmit
        }

    /**
     * Controls whether the varp is **saved on log-out** or **temporary**.
     * - When set to `true`, [scope] is set to [VarpLifetime.Perm] (varp **is saved** on log-out).
     * - When set to `false`, [scope] is set to [VarpLifetime.Temp] (varp **is not saved** on
     *   log-out).
     */
    public var permanent: Boolean
        get() = scope == VarpLifetime.Perm
        set(value) {
            val scope = if (value) VarpLifetime.Perm else VarpLifetime.Temp
            this.scope = scope
        }

    /**
     * Controls whether the varp is **temporary** or **saved on log-out**.
     * - When set to `true`, [scope] is set to [VarpLifetime.Temp] (varp **is not saved** on
     *   log-out).
     * - When set to `false`, [scope] is set to [VarpLifetime.Perm] (varp **is saved** on log-out).
     */
    public var temporary: Boolean
        get() = scope == VarpLifetime.Temp
        set(value) {
            val scope = if (value) VarpLifetime.Temp else VarpLifetime.Perm
            this.scope = scope
        }

    @InternalApi
    public fun build(id: Int): UnpackedVarpType {
        backing.internal = internal
        backing.transmit = transmit
        backing.bitProtect = collisionDetection
        return backing.build(id)
    }

    @InternalApi
    public fun defaultTransmit() {
        transmit = null
    }
}
