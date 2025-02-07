package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.michaelbull.logging.InlineLogger
import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyTo
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

fun main(args: Array<String>): Unit = GameServerLogbackCopy().main(args)

class GameServerLogbackCopy : CliktCommand(name = "logback-copy") {
    private val logger = InlineLogger()

    private val defaultResourceDir
        get() = Paths.get(".", "server", "logging", "src", "main", "resources")

    private val resourceDir: Path by
        option("--resource-dir", help = "Path to the application resources directory")
            .convert { Paths.get(it) }
            .default(defaultResourceDir)

    private val advancedLogback: Boolean by
        option("--advanced-logback", help = "Use the advanced logback configuration").flag()

    override fun run() {
        copyLogbackConfig()
    }

    private fun copyLogbackConfig() {
        val dest = resourceDir.resolve("logback.xml")

        if (dest.isRegularFile()) {
            logger.info { "Skipping logback copy: `${dest.name}` already exists." }
            return
        }

        val copyName = if (advancedLogback) "logback.advanced.xml" else "logback.novice.xml"
        val copy = resourceDir.resolve(copyName)

        if (!copy.exists()) {
            val error = "Source logback file `$copyName` not found. (${copy.absolutePathString()})"
            throw FileNotFoundException(error)
        }

        dest.createParentDirectories()
        copy.copyTo(dest, StandardCopyOption.COPY_ATTRIBUTES)
    }
}
