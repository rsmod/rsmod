package org.rsmod.api.db

public data class DatabaseConfig(
    val scheme: String,
    val path: String,
    val user: String?,
    val password: String?,
) {
    public val url: String
        get() = "$scheme$path"

    public companion object {
        public fun createSqlite(): DatabaseConfig =
            DatabaseConfig("jdbc:sqlite:", ".data/saves/game.db", null, null)
    }
}
