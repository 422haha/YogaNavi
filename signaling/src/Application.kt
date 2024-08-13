import RetrofitClient.baseUrl
import RetrofitClient.serverKey
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.header
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.time.Duration
import java.util.*

/**
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val serverApI = ServerApI()

    routing {
        get("/") {
            call.respond("Hello from WebRTC signaling server")
        }
        webSocket("/rtc") {
            val sessionID = UUID.randomUUID()

            val liveIdHeader = call.request.header("liveId") ?: ""
            val liveIsManager = call.request.header("isMyClass") ?: "0"

            System.out.println("입장 로오오그 $sessionID  $liveIdHeader $liveIsManager")

            try {
                SessionManager.onSessionStarted(sessionID, this, liveIdHeader)

                if(liveIsManager == "1")
                    serverApI.updateLiveStatus(liveIdHeader.toInt(), true)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            SessionManager.onMessage(sessionID, frame.readText(), liveIdHeader)
                        }

                        else -> Unit
                    }
                }

                if(liveIsManager == "1")
                    serverApI.updateLiveStatus(liveIdHeader.toInt(), false)

                println("Exiting incoming loop, closing session: $sessionID")
                SessionManager.onSessionClose(sessionID, liveIdHeader)
            } catch (e: ClosedReceiveChannelException) {
                println("onClose $sessionID")
                SessionManager.onSessionClose(sessionID, liveIdHeader)
            } catch (e: Throwable) {
                println("onError $sessionID $e")
                SessionManager.onSessionClose(sessionID, liveIdHeader)
            }
        }
    }
}

