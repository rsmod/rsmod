package org.rsmod.game.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import jakarta.inject.Singleton
import org.rsmod.game.config.GameConfig
import org.rsmod.game.map.Coordinates

@Singleton
public class JacksonSimpleGameModule : SimpleModule() {

    init {
        addDeserializer(GameConfig::class.java, JacksonGameConfigDeserializer)

        addSerializer(Coordinates::class.java, JacksonCoordinatesSerializer)
        addDeserializer(Coordinates::class.java, JacksonCoordinatesDeserializer)
    }
}
