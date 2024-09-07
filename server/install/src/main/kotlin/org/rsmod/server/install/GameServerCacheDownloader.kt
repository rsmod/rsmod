package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.michaelbull.logging.InlineLogger
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.zip.ZipInputStream
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteRecursively
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.moveTo
import kotlin.io.path.name
import kotlin.io.path.outputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import org.rsmod.api.core.Build
import org.rsmod.server.shared.DirectoryConstants

fun main(args: Array<String>): Unit = GameServerCacheDownloader().main(args)

class GameServerCacheDownloader : CliktCommand(name = "cache-download") {
    private val cacheUrl by option("-cacheUrl").default(Build.CACHE_URL)
    private val xteaUrl by option("-xteaUrl").default(Build.XTEA_URL)
    private val preferredCacheDir by option("-cacheBaseDir")

    private val logger = InlineLogger()

    private val cacheDir: Path
        get() = preferredCacheDir?.let { Paths.get(it) } ?: DirectoryConstants.CACHE_PATH

    private val vanillaCacheDir: Path
        get() = cacheDir.resolve("vanilla")

    private val xteaFile: Path
        get() = cacheDir.resolve("xteas.json")

    @ExperimentalPathApi
    override fun run() {
        val client = OkHttpClient()
        cacheDir.createDirectories()

        // TODO: add support for extracting .tar.gz files
        val cacheArchiveDir = vanillaCacheDir
        cacheArchiveDir.deleteRecursively()
        cacheArchiveDir.createDirectories()
        downloadAndExtractCache(client, cacheUrl, cacheArchiveDir)

        val xteaFile = xteaFile
        downloadOrFindXteaFile(client, cacheArchiveDir, xteaUrl, xteaFile)
    }

    private fun downloadAndExtractCache(client: OkHttpClient, cacheUrl: String, outputDir: Path) {
        val cacheArchiveFile = createTempArchiveFile(cacheUrl, outputDir)
        cacheArchiveFile.toFile().deleteOnExit()

        logger.info { "Downloading cache archive from: $cacheUrl" }
        downloadCache(client, cacheUrl, cacheArchiveFile)
        logger.info { "Downloaded cache archive." }

        logger.info { "Extracting cache archive files to: ${outputDir.toAbsolutePath()}" }
        extractCache(cacheArchiveFile, outputDir)
        logger.info { "Extracted cache archive files." }
    }

    private fun downloadCache(client: OkHttpClient, cacheUrl: String, archiveOutput: Path) {
        client.download(cacheUrl, archiveOutput)
    }

    private fun extractCache(archive: Path, outputDir: Path) {
        archive.extractFiles(outputDir)
    }

    private fun downloadOrFindXteaFile(
        client: OkHttpClient,
        cacheArchiveDir: Path,
        xteaUrl: String,
        output: Path,
    ): Unit =
        when {
            xteaUrl.isBlank() -> {
                logger.info {
                    "`xteaUrl` parameter not set. " +
                        "Searching for `xteas.json` file in cache archive instead..."
                }
                retrieveXteaFileFromCacheDir(cacheArchiveDir, output)
                logger.info { "Retrieved `xteas.json` file and placed into: $output" }
            }
            xteaUrl.endsWith(".json") -> {
                logger.info { "Downloading xtea file from: $xteaUrl" }
                downloadXteaFile(client, xteaUrl, output)
                logger.info { "Downloaded xtea file." }
            }
            else -> {
                logger.info { "Downloading xtea file archive from: $xteaUrl" }
                TODO("Download and extract zip file that should contain xteas.json")
            }
        }

    private fun retrieveXteaFileFromCacheDir(cacheDir: Path, outputDir: Path) {
        val files = cacheDir.listDirectoryEntries()
        val xtea = files.firstOrNull { it.name == "xteas.json" }
        if (xtea == null) {
            throw FileNotFoundException(
                "`xteas.json` file could not be found in cache archive. " +
                    "`xteaUrl` program parameter must be set."
            )
        }
        xtea.moveTo(outputDir, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
    }

    private fun downloadXteaFile(client: OkHttpClient, xteaUrl: String, outputFile: Path) {
        client.download(xteaUrl, outputFile)
    }
}

private fun createTempArchiveFile(url: String, outputDir: Path): Path {
    val cacheArchiveName = url.substringAfterLast("/")
    val cacheArchivePrefix = cacheArchiveName.substringBeforeLast(".")
    val cacheArchiveExtension = ".${cacheArchiveName.substringAfterLast(".")}"
    return createTempFile(outputDir, cacheArchivePrefix, cacheArchiveExtension)
}

private fun OkHttpClient.download(url: String, output: Path) {
    val request = Request.Builder().url(url).header("User-Agent", "rsmod").build()

    newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            throw IOException("Failed to download cache from: error=${response.code}, url=$url")
        }
        output.outputStream().use { outputStream ->
            response.body.byteStream().copyTo(outputStream)
        }
    }
}

private fun Path.extractFiles(outputDir: Path) {
    ZipInputStream(inputStream()).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                val outputFile = outputDir.resolve(File(entry.name).name)
                outputFile.outputStream(StandardOpenOption.CREATE).use { output ->
                    zip.copyTo(output)
                }
            }
            entry = zip.nextEntry
        }
    }
}
