package org.rsmod.api.server.config

public data class ServerConfig(val realm: String, val world: Int, val firstLaunch: Boolean) {
    override fun toString(): String = "ServerConfig(realm='$realm', world=$world)"
}
