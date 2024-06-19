package org.rsmod.game.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import org.rsmod.game.config.GameConfig
import org.rsmod.game.map.Coordinates
import jakarta.inject.Singleton

@Singleton
public class JacksonSimpleGameModule : SimpleModule() {

    init {
        addDeserializer(GameConfig::class.java, JacksonGameConfigDeserializer)

        addSerializer(Coordinates::class.java, JacksonCoordinatesSerializer)
        addDeserializer(Coordinates::class.java, JacksonCoordinatesDeserializer)
    }
}
