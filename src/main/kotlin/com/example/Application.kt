package com.example

import com.example.di.mainModules
import com.example.fcm.configureFCM
import com.example.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        modules(mainModules)
    }
    configureSockets()
    configureRouting()
    configureSerialization()
    configureSecurity()
    configureFCM()
}
