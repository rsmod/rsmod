package org.rsmod.api.realm

import org.rsmod.map.CoordGrid

public class Realm(public val name: String) {
    /**
     * Gets the current [RealmConfig] snapshot associated with this realm.
     *
     * **Important Note**: This value **can** and **will** be mutated through the application's
     * lifetime. It is imperative to realize this and avoid caching any values from the config, or
     * to take this fact into account if you do so.
     *
     * During the application's initial startup, this value will be inaccessible (to prevent misuse)
     * until [updateConfig] is called. Callers should only expect this value to be available _after_
     * the application services have started up.
     *
     * @throws IllegalStateException if [updateConfig] has not been called yet.
     */
    public val config: RealmConfig
        get() = getConfigOrThrow()

    private lateinit var mutableConfig: RealmConfig

    public fun updateConfig(config: RealmConfig) {
        this.mutableConfig = config
    }

    private fun getConfigOrThrow(): RealmConfig {
        check(::mutableConfig.isInitialized) { "Realm config has not been initialized yet." }
        return mutableConfig
    }
}

/**
 * @param requireRegistration If `true`, players must have a pre-registered account in the database
 *   to log in. If `false`, new accounts will be automatically created on first login.
 * @param ignorePasswords If `true`, any password will be accepted for any account without
 *   verification. Note: This flag may only take effect in certain environments (e.g., [devMode] is
 *   `true`), depending on the login server's safeguards.
 * @param autoAssignDisplayNames If `true`, player display names will be automatically assigned
 *   based on their login name.
 */
public data class RealmConfig(
    val id: Int,
    val loginMessage: String?,
    val xpRate: Int,
    val spawnCoord: CoordGrid,
    val respawnCoord: CoordGrid,
    val devMode: Boolean,
    val requireRegistration: Boolean,
    val ignorePasswords: Boolean,
    val autoAssignDisplayNames: Boolean,
)
