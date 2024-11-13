package com.example.fcm

import com.example.data.model.FCMRequest
import com.example.data.model.FcmData
import com.google.auth.oauth2.ServiceAccountCredentials
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.json
import java.io.FileNotFoundException
import java.io.InputStream

fun Application.configureFCM() {
    routing {
        post("fcm/send") {
            val request = call.receive<FCMRequest>()
            var fcmToken = request.message.token
            val title = "ktor"
            val message = "ktor messaging"
            val data = request.message.data
            val projectId = "fir-b8a47"
            val inputStream = this::class.java.classLoader.getResourceAsStream("info.json")
                ?: throw FileNotFoundException("Resource not found: info.json")
            val accessToken = getAccessToken(inputStream)
            val response = sendFcmToken(
                fcmToken = fcmToken,
                title = title,
                message = message,
                customData = data,
                projectId = projectId,
                accessToken = accessToken
            )
            if(response.status== HttpStatusCode.OK){
                call.respond(request)

            }
        }
    }
}

suspend fun sendFcmToken(
    fcmToken: String,
    title: String,
    message: String,
    customData: FcmData?,
    projectId: String,
    accessToken: String
): HttpResponse {
    val client = HttpClient(CIO)
    val response = client.post("https://fcm.googleapis.com/v1/projects/$projectId/messages:send") {
        header("Authorization", "Bearer $accessToken")
        contentType(ContentType.Application.Json)
        setBody(
            """
                    {
                    "message":{
                        "token":"$fcmToken",
                        "data":{
                        "title":"$title",
                        "message":"$message",
                        "data":"${customData ?: ""}"
                        },
                        "android":{
                        "priority":"High"
                        }
                        }
                    }
                """.trimIndent()
        )
    }
    client.close()

    return response
}
suspend fun getAccessToken(inputStream: InputStream):String=
    withContext(Dispatchers.IO) {
        val credentials = ServiceAccountCredentials
            .fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
        credentials.refreshIfExpired()
        return@withContext credentials.accessToken.tokenValue
    }