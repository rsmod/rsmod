package org.rsmod.api.realm

public enum class Realm(public val id: Int, public val dbName: String) {
    Dev(id = 1, dbName = "dev"),
    Main(id = 2, dbName = "main");

    public val isDev: Boolean
        get() = this == Dev
}
