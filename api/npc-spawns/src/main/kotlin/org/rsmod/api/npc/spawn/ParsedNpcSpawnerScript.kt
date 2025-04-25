package org.rsmod.api.npc.spawn

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.script.onGameStartup
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.scheduler.TaskScheduler

public class ParsedNpcSpawnerScript
@Inject
constructor(private val spawner: ParsedNpcSpawner, private val scheduler: TaskScheduler) :
    PluginScript() {
    private val logger = InlineLogger()

    private lateinit var staticSpawns: Collection<ParsedNpcSpawn>

    override fun ScriptContext.startup() {
        scheduler.scheduleStaticSpawns()
        onGameStartup { spawnStaticSpawns() }
    }

    private fun spawnStaticSpawns() {
        check(::staticSpawns.isInitialized) { "`staticSpawns` must be set." }
        logger.debug { "Spawning static npcs..." }
        spawner.spawnAll(staticSpawns)
        logger.info {
            "Spawned ${staticSpawns.size} static npc${if (staticSpawns.size == 1) "" else "s"}."
        }
    }

    private fun TaskScheduler.scheduleStaticSpawns() = scheduleIO { loadStaticSpawns() }

    private suspend fun loadStaticSpawns() {
        val spawnListCount = spawner.inputContentsList.size
        val spawns = spawner.loadStaticSpawns()
        staticSpawns = spawns
        logger.debug {
            "Loaded $spawnListCount spawn list${if (spawnListCount == 1) "" else "s"} " +
                "with a total of ${spawns.size} npc spawn${if (spawns.size == 1) "" else "s"}."
        }
        // No longer need these file content getters, can discard.
        spawner.inputContentsList.clear()
    }
}
