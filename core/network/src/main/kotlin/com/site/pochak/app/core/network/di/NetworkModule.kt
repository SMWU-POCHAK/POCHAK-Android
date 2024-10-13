package com.site.pochak.app.core.network.di

import com.site.pochak.app.core.network.BaseUrlRetrofit
import com.site.pochak.app.core.network.BaseUrlV2Retrofit
import com.site.pochak.app.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()

    @BaseUrlRetrofit
    @Provides
    @Singleton
    fun providesPochakRetrofit(
        json: Json,
        okHttpCallFactory: Call.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .callFactory(okHttpCallFactory)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @BaseUrlV2Retrofit
    @Provides
    @Singleton
    fun providesPochakV2Retrofit(
        json: Json,
        okHttpCallFactory: Call.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL_V2)
            .callFactory(okHttpCallFactory)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}