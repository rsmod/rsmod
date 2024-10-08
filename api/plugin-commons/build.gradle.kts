plugins {
    id("base-conventions")
}

dependencies {
    api(libs.guice)
    api(libs.bundles.logging)
    api(libs.kotlin.coroutines.core)
    api(projects.api.cache)
    api(projects.api.cheat)
    api(projects.api.config)
    api(projects.api.dialogue)
    api(projects.api.gameProcess)
    api(projects.api.invtx)
    api(projects.api.npc)
    api(projects.api.npcSpawns)
    api(projects.api.objSpawns)
    api(projects.api.player)
    api(projects.api.random)
    api(projects.api.repo)
    api(projects.api.route)
    api(projects.api.script)
    api(projects.api.shops)
    api(projects.api.type.typeBuilders)
    api(projects.api.type.typeEditors)
    api(projects.api.type.typeReferences)
    api(projects.api.type.typeScriptDsl)
    api(projects.api.utils.utilsFormat)
    api(projects.api.utils.utilsIo)
    api(projects.engine.coroutine)
    api(projects.engine.events)
    api(projects.engine.game)
    api(projects.engine.map)
    api(projects.engine.objtx)
    api(projects.engine.pathfinder)
    api(projects.engine.plugin)
    api(projects.engine.scheduler)
}
