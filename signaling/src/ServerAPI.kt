import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

data class YogaDetailResponse<T>(val data: List<T>, val message: String)

interface InfoAPI {
    @PUT("home/update")
    suspend fun updateLiveStatus(@Body dto: SignalingDto,
                                 @Header("SIGNALING-API-KEY") token: String):
            retrofit2.Response<YogaDetailResponse<Unit>>
}

object RetrofitClient {
    val serverKey = myLocalSecret.getProperty("SERVER_KEY") ?: ""
    val baseUrl = myLocalSecret.getProperty("BASE_URL") ?: ""

    val instance: InfoAPI by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InfoAPI::class.java)
    }

    fun getApiKey(): String = serverKey
}

class ServerApI {
    private val apiService = RetrofitClient.instance

    suspend fun updateLiveStatus(liveId: Int, isOnAir: Boolean) {
        try {
            val result = apiService.updateLiveStatus(
                dto = SignalingDto(liveId = liveId, onAir = isOnAir),
                token = RetrofitClient.getApiKey())
            println("$result")
        } catch (e: Exception) {
            println("Error fetching users: ${e.message}")
        }
    }
}