package org.rsmod.api.realm.config

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.realm.Realm
import org.rsmod.server.services.ListenerService

public class RealmConfigService
@Inject
constructor(private val realm: Realm, private val loader: RealmConfigLoader) : ListenerService {
    private val logger = InlineLogger()

    override suspend fun signalStartup() {
        logger.debug { "Loading realm '${realm.name}' configuration..." }
        val config = loader.load(realm.name)
        if (config == null) {
            throw IllegalStateException("Realm not found in database: '${realm.name}'")
        }
        realm.updateConfig(config)
        logger.info { "Loaded realm '${realm.name}' configuration: $config" }
    }

    override suspend fun signalShutdown() {}

    override suspend fun startup() {}

    override suspend fun shutdown() {}
}
