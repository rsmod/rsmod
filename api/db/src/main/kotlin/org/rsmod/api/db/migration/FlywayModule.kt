package org.rsmod.api.db.migration

import org.rsmod.module.ExtendedModule

public object FlywayModule : ExtendedModule() {
    override fun bind() {
        bindInstance<FlywayMigration>()
    }
}
