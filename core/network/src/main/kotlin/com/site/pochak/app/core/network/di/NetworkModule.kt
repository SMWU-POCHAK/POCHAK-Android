package com.site.pochak.app.core.network.di

import com.site.pochak.app.core.network.BuildConfig
import com.site.pochak.app.core.network.service.AlarmService
import com.site.pochak.app.core.network.service.BlockService
import com.site.pochak.app.core.network.service.CommentService
import com.site.pochak.app.core.network.service.FollowService
import com.site.pochak.app.core.network.service.LikeService
import com.site.pochak.app.core.network.service.LoginService
import com.site.pochak.app.core.network.service.MemoriesService
import com.site.pochak.app.core.network.service.PostService
import com.site.pochak.app.core.network.service.ProfileService
import com.site.pochak.app.core.network.service.ReportService
import com.site.pochak.app.core.network.service.SearchService
import com.site.pochak.app.core.network.service.TagService
import com.site.pochak.app.core.network.token.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.internal.platform.android.AndroidLogHandler.setLevel
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
    fun okHttpCallFactory(authInterceptor: AuthInterceptor): Call.Factory =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .addInterceptor(authInterceptor)
            .build()

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

    @Provides
    @Singleton
    fun providesAlarmService(retrofit: Retrofit): AlarmService =
        retrofit.create(AlarmService::class.java)

    @Provides
    @Singleton
    fun providesBlockService(retrofit: Retrofit): BlockService =
        retrofit.create(BlockService::class.java)

    @Provides
    @Singleton
    fun providesCommentService(retrofit: Retrofit): CommentService =
        retrofit.create(CommentService::class.java)

    @Provides
    @Singleton
    fun providesFollowService(retrofit: Retrofit): FollowService =
        retrofit.create(FollowService::class.java)

    @Provides
    @Singleton
    fun providesLikeService(retrofit: Retrofit): LikeService =
        retrofit.create(LikeService::class.java)

    @Provides
    @Singleton
    fun providesLoginService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun providesMemoriesService(retrofit: Retrofit): MemoriesService =
        retrofit.create(MemoriesService::class.java)

    @Provides
    @Singleton
    fun providesPostService(retrofit: Retrofit): PostService =
        retrofit.create(PostService::class.java)

    @Provides
    @Singleton
    fun providesProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun providesReportService(retrofit: Retrofit): ReportService =
        retrofit.create(ReportService::class.java)

    @Provides
    @Singleton
    fun providesSearchService(retrofit: Retrofit): SearchService =
        retrofit.create(SearchService::class.java)

    @Provides
    @Singleton
    fun providesTagService(retrofit: Retrofit): TagService =
        retrofit.create(TagService::class.java)
}