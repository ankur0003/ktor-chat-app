package com.example.controller

import com.example.MemberExistsException
import com.example.data.MessageDataSource
import com.example.data.model.Member
import com.example.data.model.Message
import io.ktor.util.*
import io.ktor.websocket.*
import jdk.management.jfr.FlightRecorderMXBean
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.jvm.internal.impl.util.MemberKindCheck
import io.ktor.websocket.*
class RoomController(private val dataSource: MessageDataSource) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(username: String, sessionId: String, socket: WebSocketSession) {
        if (members.containsKey(username)) {
            throw MemberExistsException()
        }
        members[username] = Member(username, sessionId, socket)

    }

    suspend fun sendMessage(senderUsername: String, message: String) {
        members.values.forEach { member ->
            val msg = Message(text = message, username = senderUsername, timestamp = System.currentTimeMillis())
            dataSource.insertMessage(msg)
            val parseMsg = Json.encodeToString(msg)
            member.socket.send(Frame.Text(parseMsg))

        }
    }

    suspend fun sendPong(senderUsername: String, frame: Frame) {

        members.values.forEach {
            when (frame) {
                is Frame.Binary -> {
                    it.socket.send(Frame.Binary(true,frame.buffer))
                }

                is Frame.Ping -> {
                    it.socket.send(Frame.Pong(frame.buffer))

                }

                else -> {
                    it.socket.send(frame)
                }
            }

        }
    }

    suspend fun getAllMessages(): List<Message> = dataSource.getAllMessages()

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "try-again"))
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}