package org.capturecoop.buildboy

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File

@Serializable
data class Commit(
    val hash: String = "%h",
    val author: String = "%an",
    val timestamp: String = "%at",
    val subject: String = "%f"
)

object ExecUtils {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private const val SPLITTER = "_-_split_-_"
    private val jsonWithDefaults = Json { encodeDefaults = true }
    private val jsonPretty = Json { prettyPrint = true }

    fun exec(command: String, directory: File): String {
        logger.debug("exec(command=$command, directory=$directory)")
        val process = ProcessBuilder()
            .redirectErrorStream(true)
            .command(command.split(" "))
            .directory(directory)
            .start()
        var output = ""
        process.inputReader().lines().forEach { output += it }
        val result = process.waitFor()
        if(result != 0) throw Exception("Return code: $result")
        return output
    }

    fun getCommits(repoFolder: File, amount: Int = Integer.MAX_VALUE): List<Commit> {
        val baseJson = jsonWithDefaults
            .encodeToString(Commit())
            .replace("\"", "\\\"")

        return exec("git log -$amount --format=\"$baseJson$SPLITTER\"", repoFolder)
            .split(SPLITTER)
            .filter { it.isNotEmpty() }
            .map { Json.decodeFromString<Commit>(it) }
    }

    fun getLatestCommit(repoFolder: File) = getCommits(repoFolder, 1).first()

    fun getCommitsAsJson(repoFolder: File, amount: Int = Integer.MAX_VALUE): String {
        return jsonPretty.encodeToString(getCommits(repoFolder, amount))
    }
}