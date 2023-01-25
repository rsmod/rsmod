package org.rsmod.game.config.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import org.rsmod.game.config.GameConfig
import javax.inject.Singleton

@Singleton
public class JacksonConfigModule : SimpleModule() {

    init {
        addDeserializer(GameConfig::class.java, JacksonGameConfigDeserializer)
    }
}
