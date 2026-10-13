package org.runerealm.content.interfaces.prayer.tab

import com.google.inject.Provider
import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.module.PluginModule

class PrayerModule : PluginModule() {
    override fun bind() {
        bindProvider(PrayerRepositoryProvider::class.java)
    }
}

private class PrayerRepositoryProvider
@Inject
constructor(private val enumResolver: EnumTypeMapResolver, private val objTypes: ObjTypeList) :
    Provider<PrayerRepository> {
    override fun get(): PrayerRepository {
        val repo = PrayerRepository(enumResolver, objTypes)
        repo.load()
        return repo
    }
}
