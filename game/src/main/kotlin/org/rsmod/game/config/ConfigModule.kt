package org.rsmod.game.config

import com.fasterxml.jackson.databind.Module
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.rsmod.game.config.jackson.JacksonConfigModule
import org.rsmod.toml.TomlModule

public object ConfigModule : AbstractModule() {

    override fun configure() {
        install(TomlModule)

        bind(GameConfig::class.java)
            .toProvider(GameConfigProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(RSAPrivateCrtKeyParameters::class.java)
            .toProvider(RsaKeyProvider::class.java)
            .`in`(Scopes.SINGLETON)

        Multibinder.newSetBinder(binder(), Module::class.java)
            .addBinding()
            .to(JacksonConfigModule::class.java)
    }
}
