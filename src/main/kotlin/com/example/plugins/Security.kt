package com.example.plugins

import com.example.session.ChatSession
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.*

fun Application.configureSecurity() {
    install(Sessions) {
    cookie<ChatSession >("SESSION")
    }
    intercept(Plugins){
        if(call.sessions.get<ChatSession>()==null){
            call.sessions.set(ChatSession(call.parameters["username"]?:"Guest", generateNonce()))
        }
    }
}
