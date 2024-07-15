package com.ssafy.yoganavi.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.ssafy.yoganavi.data.auth.AuthInterceptor
import com.ssafy.yoganavi.data.source.UserAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val timeout = 5000L

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .readTimeout(timeout, TimeUnit.MILLISECONDS)
        .connectTimeout(timeout, TimeUnit.MILLISECONDS)
        .build()


    @Singleton
    @Provides
    fun provideUserRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://122.199.110.160:25565")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideUserAPI(retrofit: Retrofit): UserAPI = retrofit.create(UserAPI::class.java)
}