package com.example.di

import com.example.controller.RoomController
import com.example.data.MessageDataSource
import com.example.data.MessageDataSourceImpl
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModules = module {
    single {
        KMongo.createClient().coroutine.getDatabase("message_db")

    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single {
        RoomController(get())
    }
}