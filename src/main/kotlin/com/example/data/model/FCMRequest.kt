package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FCMRequest(val message: FcmMessage)

@Serializable
data class FcmMessage(val token: String, val notification: FcmNotification, val data: FcmData)

@Serializable
data class FcmNotification(val body: String, val title: String)

@Serializable
data class FcmData(val action: String)