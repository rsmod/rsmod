package org.rsmod.config

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters

public object ConfigModule : AbstractModule() {

    override fun configure() {
        bind(RSAPrivateCrtKeyParameters::class.java)
            .toProvider(RsaKeyProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
