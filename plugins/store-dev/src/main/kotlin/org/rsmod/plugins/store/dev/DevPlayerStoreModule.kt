package org.rsmod.plugins.store.dev

import com.google.inject.Scopes
import com.google.inject.TypeLiteral
import org.rsmod.game.scripts.module.DevModule
import org.rsmod.plugins.store.dev.data.DevJsonPlayerCodec
import org.rsmod.plugins.store.dev.data.DevPlayerDataMapper
import org.rsmod.plugins.store.player.PlayerCodec
import org.rsmod.plugins.store.player.PlayerDataMapper

public object DevPlayerStoreModule : DevModule() {

    private val DATA_MAPPER_TYPE = object : TypeLiteral<PlayerDataMapper<*>>() {}
    private val JSON_CODEC_TYPE = object : TypeLiteral<DevJsonPlayerCodec<*>>() {}

    override fun configure() {
        bind(DATA_MAPPER_TYPE)
            .to(DevPlayerDataMapper::class.java)
            .`in`(Scopes.SINGLETON)

        bind(PlayerCodec::class.java)
            .to(JSON_CODEC_TYPE)
            .`in`(Scopes.SINGLETON)
    }
}
