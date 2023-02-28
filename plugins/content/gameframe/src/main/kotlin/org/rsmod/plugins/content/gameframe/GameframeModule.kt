package org.rsmod.plugins.content.gameframe

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.plugins.content.gameframe.build.GameframeResizeList

public object GameframeModule : AbstractModule() {

    override fun configure() {
        bind(GameframeResizeList::class.java).`in`(Scopes.SINGLETON)
    }
}
