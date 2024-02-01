package org.capturecoop.buildboy

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

object Requestor {
    val serializer =  Json { ignoreUnknownKeys = true }
    val httpClient = HttpClient(CIO)

    suspend inline fun <reified T> get(url: String) = serializer.decodeFromString<T>(httpClient.get(url).bodyAsText())
}