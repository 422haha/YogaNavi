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
    private lateinit var API_KEY: String
    private lateinit var BASE_URL: String

    fun initialize(apiKey: String, baseUrl: String) {
        API_KEY = apiKey
        BASE_URL = baseUrl
        println("SERVER_KEY: $API_KEY")
        println("BASE_URL: $BASE_URL")
    }

    val instance: InfoAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InfoAPI::class.java)
    }

    fun getApiKey(): String = API_KEY
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