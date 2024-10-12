import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

/**
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */

object SessionManager {
    private val sessionManagerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()

    private val rooms = mutableMapOf<String, MutableList<UUID>>()
    private val clients = mutableMapOf<UUID, DefaultWebSocketServerSession>()
    private val roomStates = mutableMapOf<String, WebRTCSessionState>()
    private val clientStates = mutableMapOf<UUID, ClientState>()
    private val offerTimeouts = mutableMapOf<String, Job>()

    suspend fun onSessionStarted(
        sessionId: UUID,
        session: DefaultWebSocketServerSession,
        roomId: String) {
        mutex.withLock {
            if ((rooms[roomId]?.size ?: 0) >= 2) {
                session.send(Frame.Close())
                return
            }

            rooms.getOrPut(roomId) { mutableListOf() }.add(sessionId)
            clients[sessionId] = session
            clientStates[sessionId] = ClientState.CONNECTED

            println("세션 로오오그 $sessionId  $roomId")
            println("사이즈 로오오그 ${rooms[roomId]?.size}")

            session.send("Added as a client: $sessionId")
            updateRoomState(roomId)
        }
    }

    private suspend fun updateRoomState(roomId: String) {
        val roomSize = rooms[roomId]?.size ?: 0

        roomStates[roomId] = when (roomSize) {
            2 -> WebRTCSessionState.Ready
            1 -> WebRTCSessionState.Impossible
            else -> WebRTCSessionState.Impossible
        }
        notifyAboutStateUpdate(roomId)
    }

    suspend fun onMessage(sessionId: UUID, message: String, roomId: String) {
        when {
            message.startsWith(MessageType.STATE.toString(), true) -> handleState(sessionId, roomId)
            message.startsWith(MessageType.OFFER.toString(), true) -> handleOffer(
                sessionId,
                message,
                roomId
            )

            message.startsWith(MessageType.ANSWER.toString(), true) -> handleAnswer(
                sessionId,
                message,
                roomId
            )

            message.startsWith(MessageType.ICE.toString(), true) -> handleIce(
                sessionId,
                message,
                roomId
            )
        }
    }

    private suspend fun handleState(sessionId: UUID, roomId: String) {
        clients[sessionId]?.send("${MessageType.STATE} ${roomStates[roomId]}")
    }

    private suspend fun handleOffer(sessionId: UUID, message: String, roomId: String) {
        if (roomStates[roomId] != WebRTCSessionState.Ready) {
            return
        }
        roomStates[roomId] = WebRTCSessionState.Creating
        println("handling offer from $sessionId")
        notifyAboutStateUpdate(roomId)
        val otherClient = rooms[roomId]?.find { it != sessionId }?.let { clients[it] }
        otherClient?.send(message)
        clientStates[sessionId] = ClientState.OFFER_SENT

        // Set a timeout for the answer
        offerTimeouts[roomId] = sessionManagerScope.launch {
            delay(10000) // 10 seconds timeout
            if (roomStates[roomId] == WebRTCSessionState.Creating) {
                println("Answer timeout for room $roomId")
                resetRoom(roomId)
            }
        }
    }

    private suspend fun handleAnswer(sessionId: UUID, message: String, roomId: String) {
        if (roomStates[roomId] != WebRTCSessionState.Creating) {
            return
        }
        println("handling answer from $sessionId")
        val otherClient = rooms[roomId]?.find { it != sessionId }?.let { clients[it] }
        otherClient?.send(message)
        roomStates[roomId] = WebRTCSessionState.Active
        clientStates[sessionId] = ClientState.CONNECTED
        notifyAboutStateUpdate(roomId)

        // Cancel the offer timeout
        offerTimeouts[roomId]?.cancel()
        offerTimeouts.remove(roomId)
    }

    private suspend fun handleIce(sessionId: UUID, message: String, roomId: String) {
        println("handling ice from $sessionId")
        val otherClient = rooms[roomId]?.find { it != sessionId }?.let { clients[it] }
        otherClient?.send(message)
    }

    suspend fun onSessionClose(sessionId: UUID, roomId: String) {
        mutex.withLock {
            clients.remove(sessionId)
            clientStates.remove(sessionId)
            rooms[roomId]?.remove(sessionId)
            if (rooms[roomId].isNullOrEmpty()) {
                rooms.remove(roomId)
                roomStates.remove(roomId)
                offerTimeouts[roomId]?.cancel()
                offerTimeouts.remove(roomId)
            } else {
                resetRoom(roomId)
            }
        }
    }

    private suspend fun resetRoom(roomId: String) {
        roomStates[roomId] = WebRTCSessionState.Impossible

        rooms[roomId]?.forEach { sessionId ->
            clientStates[sessionId] = ClientState.CONNECTED
        }
        updateRoomState(roomId)
    }

    private suspend fun notifyAboutStateUpdate(roomId: String) {
        val state = roomStates[roomId] ?: WebRTCSessionState.Impossible

        rooms[roomId]?.forEach { sessionId ->
            println("상태 로오오그$sessionId $state")

            clients[sessionId]?.send("${MessageType.STATE} $state")
        }
    }

    private suspend fun DefaultWebSocketServerSession.send(message: String) {
        send(Frame.Text(message))
    }

    enum class WebRTCSessionState {
        Active, Creating, Ready, Impossible
    }

    enum class ClientState {
        CONNECTED, OFFER_SENT
    }

    enum class MessageType {
        STATE, OFFER, ANSWER, ICE
    }
}
