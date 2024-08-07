package com.ssafy.yoganavi.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.ssafy.yoganavi.data.auth.AuthInterceptor
import com.ssafy.yoganavi.data.repository.dataStore.DataStoreRepository
import com.ssafy.yoganavi.data.source.info.InfoAPI
import com.ssafy.yoganavi.data.source.lecture.LectureAPI
import com.ssafy.yoganavi.data.source.user.UserAPI
import com.ssafy.yoganavi.ui.utils.AuthManager
import com.ssafy.yoganavi.ui.utils.TIME_OUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        dataStoreRepository: DataStoreRepository,
        authManager: AuthManager
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(dataStoreRepository, authManager))
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .build()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Singleton
    @Provides
    fun provideUserRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://i11d210.p.ssafy.io:8080")
//        .baseUrl("http://192.168.100.97:8080")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideUserAPI(retrofit: Retrofit): UserAPI = retrofit.create(UserAPI::class.java)

    @Singleton
    @Provides
    fun provideInfoAPI(retrofit: Retrofit): InfoAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideLectureAPI(retrofit: Retrofit): LectureAPI = retrofit.create()
}