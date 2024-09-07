package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.type.mod.ModLevel

@DslMarker private annotation class ModGroupBuilderDsl

@ModGroupBuilderDsl
public class ModGroupPluginBuilder(private var internal: String? = null) {
    public var moderator: Boolean? = null
    public var administrator: Boolean? = null
    public var modLevels: Set<ModLevel>? = null

    public var modLevel: ModLevel? = null
        set(value) {
            modLevels = value?.let { setOf(it) }
        }

    public fun build(id: Int): ModGroup {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val moderator = moderator == true
        val administrator = administrator == true
        val modLevels = modLevels ?: emptySet()
        return ModGroup(id, internal, moderator, administrator, modLevels)
    }

    public operator fun ModLevel.plus(other: ModLevel): Set<ModLevel> = setOf(this, other)
}
