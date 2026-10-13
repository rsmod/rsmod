package org.runerealm.api.core.module

import com.google.inject.multibindings.Multibinder
import org.runerealm.api.drop.table.NpcDropTableProvider
import org.runerealm.api.drop.table.NpcDropTables
import org.runerealm.module.ExtendedModule

public object DropTableModule : ExtendedModule() {
    override fun bind() {
        Multibinder.newSetBinder(binder(), NpcDropTableProvider::class.java)
        bindInstance<NpcDropTables>()
    }
}
