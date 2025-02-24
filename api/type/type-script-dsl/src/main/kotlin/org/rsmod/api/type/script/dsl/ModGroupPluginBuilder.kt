package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.type.mod.ModLevel

@DslMarker private annotation class ModGroupBuilderDsl

@ModGroupBuilderDsl
public class ModGroupPluginBuilder(private var internal: String? = null) {
    public var clientModerator: Boolean? = null
    public var clientAdministrator: Boolean? = null
    public var modLevels: Set<ModLevel>? = null

    public var modLevel: ModLevel? = null
        set(value) {
            modLevels = value?.let { setOf(it) }
            field = value
        }

    public fun build(id: Int): ModGroup {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val moderator = clientModerator == true
        val administrator = clientAdministrator == true
        val modLevels = modLevels ?: emptySet()
        return ModGroup(modLevels, moderator, administrator, id, internal)
    }

    public operator fun ModLevel.plus(other: ModLevel): Set<ModLevel> = setOf(this, other)
}
