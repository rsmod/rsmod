package org.rsmod.server.app.modules

import org.rsmod.api.parsers.jackson.JacksonModule
import org.rsmod.api.parsers.json.JsonModule
import org.rsmod.api.parsers.toml.TomlModule
import org.rsmod.module.ExtendedModule

object ParserModule : ExtendedModule() {
    override fun bind() {
        install(JacksonModule)
        install(JsonModule)
        install(TomlModule)
    }
}
