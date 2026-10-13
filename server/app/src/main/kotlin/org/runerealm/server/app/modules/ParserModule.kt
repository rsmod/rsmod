package org.runerealm.server.app.modules

import org.runerealm.api.parsers.jackson.JacksonModule
import org.runerealm.api.parsers.json.JsonModule
import org.runerealm.api.parsers.toml.TomlModule
import org.runerealm.module.ExtendedModule

object ParserModule : ExtendedModule() {
    override fun bind() {
        install(JacksonModule)
        install(JsonModule)
        install(TomlModule)
    }
}
