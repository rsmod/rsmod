package org.rsmod.config

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.rsmod.toml.TomlModule

public object ConfigModule : AbstractModule() {

    override fun configure() {
        install(TomlModule)

        bind(RSAPrivateCrtKeyParameters::class.java)
            .toProvider(RsaKeyProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
