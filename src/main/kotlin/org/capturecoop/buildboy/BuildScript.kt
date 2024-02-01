package org.capturecoop.buildboy

import kotlinx.serialization.Serializable

@Serializable
data class BuildScript(
    val repo: String,
    val repoName: String,
    val branch: String,
    val buildDir: String,
    val preBuildScripts: List<String> = emptyList(),
    val buildScript: String
)