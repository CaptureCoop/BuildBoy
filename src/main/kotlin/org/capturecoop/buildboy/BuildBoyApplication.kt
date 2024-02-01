package org.capturecoop.buildboy

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

class BuildBoyApplication {
    private val logger = LoggerFactory.getLogger("BuildBoy")
    private val rootFolder = File("BuildBoy").apply { mkdir() }
    private val reposFolder = File(rootFolder, "repos").apply { mkdir() }
    private val buildScriptsFolder = File(rootFolder, "buildscripts").apply { mkdir() }
    private val buildsFolder = File(rootFolder, "builds").apply { mkdir() }

    private val buildJsonWriter = Json { prettyPrint = true }

    private val buildScripts = buildScriptsFolder
        .listFiles()
        ?.filter { it.extension == "json" }
        ?.map { Json.decodeFromString<BuildScript>(it.readText()) } ?: emptyList()

    init {
        logger.info("Loaded buildscripts:")
        buildScripts.forEach { logger.info(it.toString()) }
    }

    fun scanAll() {
        buildScripts.forEach(::checkAndBuild)
    }

    @Serializable
    data class BuildInfo(
        val hash: String,
        val author: String,
        val timestamp: String,
        val message: String,
        val version: String
    )

    private fun checkAndBuild(buildScript: BuildScript) {
        val repoFolder = File(reposFolder, "${buildScript.repoName}-${buildScript.branch}/${buildScript.repoName}")
        if(!repoFolder.exists()) {
            repoFolder.mkdirs()
            ExecUtils.exec("git clone -b ${buildScript.branch} ${buildScript.repo}", repoFolder.parentFile)
        } else {
            ExecUtils.exec("git pull", repoFolder)
        }

        val latest = ExecUtils.getLatestCommit(repoFolder)
        val version = File(repoFolder, "version.txt").readText()
        val buildFolder = File(buildsFolder, "${buildScript.repoName}/${buildScript.branch}/build-${latest.hash}-$version")
        if(!buildFolder.exists() || (buildFolder.listFiles() ?: emptyArray()).isEmpty()) {
            buildScript.preBuildScripts.forEach { command -> ExecUtils.exec(command, repoFolder) }
            ExecUtils.exec(buildScript.buildScript, repoFolder)

            File(repoFolder, buildScript.buildDir).copyRecursively(buildFolder)
            val buildInfo = BuildInfo(
                hash = latest.hash,
                author = latest.author,
                timestamp = latest.timestamp,
                message = latest.subject,
                version = version
            )
            File(buildFolder, "build.json").writeText(buildJsonWriter.encodeToString(buildInfo))
        }
    }
}