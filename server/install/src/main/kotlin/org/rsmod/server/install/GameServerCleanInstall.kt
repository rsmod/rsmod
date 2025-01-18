package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.michaelbull.logging.InlineLogger
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import org.rsmod.server.shared.DirectoryConstants

fun main(args: Array<String>): Unit = GameServerCleanInstall().main(args)

class GameServerCleanInstall : CliktCommand(name = "clean-install") {
    private val logger = InlineLogger()

    private val cacheDir: Path
        get() = DirectoryConstants.CACHE_PATH

    private val dataDir: Path
        get() = DirectoryConstants.DATA_PATH

    @OptIn(ExperimentalPathApi::class)
    override fun run() {
        deleteCacheDir()
        deleteRsaKeys()
    }

    @OptIn(ExperimentalPathApi::class)
    private fun deleteCacheDir(dir: Path = cacheDir) {
        if (!dir.isDirectory()) {
            logger.info { "Cache folder not found, skipping..." }
            return
        }
        logger.info { "Cleaning up folder ${dir.toAbsolutePath()}" }
        try {
            cacheDir.deleteRecursively()
        } catch (e: Exception) {
            logger.error(e) { "Could not delete folder (may be in use): $dir" }
            throw e
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun deleteRsaKeys(dir: Path = dataDir) {
        val keys =
            listOf(dir.resolve("game.key"), dir.resolve("client.key")).filter(Path::isRegularFile)
        if (keys.isEmpty()) {
            logger.info { "RSA files not found, skipping..." }
            return
        }
        logger.info { "Deleting RSA keys in folder ${dir.toAbsolutePath()}" }
        try {
            keys.forEach(Path::deleteRecursively)
        } catch (e: Exception) {
            logger.error(e) { "Could not delete files (may be in use): $dir" }
            throw e
        }
    }
}
