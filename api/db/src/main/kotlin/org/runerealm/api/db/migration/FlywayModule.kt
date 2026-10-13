package org.runerealm.api.db.migration

import org.runerealm.module.ExtendedModule

public object FlywayModule : ExtendedModule() {
    override fun bind() {
        bindInstance<FlywayMigration>()
    }
}
