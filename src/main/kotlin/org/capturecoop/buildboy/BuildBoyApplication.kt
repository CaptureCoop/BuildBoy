package org.capturecoop.buildboy

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

class BuildBoyApplication() {
    private val logger = LoggerFactory.getLogger("BuildBoy")
    private val rootFolder = File("BuildBoy").apply { mkdir() }
    private val reposFolder = File(rootFolder, "repos").apply { mkdir() }
    private val buildScriptsFolder = File(rootFolder, "buildscripts").apply { mkdir() }

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

    private fun checkAndBuild(buildScript: BuildScript) {
        //val baseFolder = //Files.createTempDirectory("BuildRepo").toFile()
        val repoFolder = File(reposFolder, buildScript.repoName).apply { mkdir() }
        //if(!repoFolder.exists()) {
            //repoFolder.mkdir()
            val t = ExecUtils.exec("git status", repoFolder)
            println(t)
        //}
        println(repoFolder)

        //val repoFolder = File(baseFolder, buildScript.repoName)
        //val libsFolder = File(repoFolder, buildScript.buildDir)
//
        //ExecUtils.exec("git clone -b ${buildScript.branch} ${buildScript.repo}", baseFolder)
        //val latest = ExecUtils.getLatestCommit(repoFolder)
        //val version = File(repoFolder, "version.txt").readText()
        //val name = "build-${latest.hash}-$version"
        //println(name)
        //println("Timestamp: $timestamp")
        //buildScript.preBuildScripts.forEach { command -> ExecUtils.exec(command, repoFolder) }
        //ExecUtils.exec(buildScript.buildScript, repoFolder)
        //println("Successfully built: ${libsFolder.listFiles()?.map { it.name }}")
        //Desktop.getDesktop().open(libsFolder)
    }
}