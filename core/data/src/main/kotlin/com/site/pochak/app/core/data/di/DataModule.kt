package com.site.pochak.app.core.data.di

import com.site.pochak.app.core.data.repository.AlarmRepository
import com.site.pochak.app.core.data.repository.AlarmRepositoryImpl
import com.site.pochak.app.core.data.repository.BlockRepository
import com.site.pochak.app.core.data.repository.BlockRepositoryImpl
import com.site.pochak.app.core.data.repository.CommentRepository
import com.site.pochak.app.core.data.repository.CommentRepositoryImpl
import com.site.pochak.app.core.data.repository.FcmRepository
import com.site.pochak.app.core.data.repository.FcmRepositoryImpl
import com.site.pochak.app.core.data.repository.FollowRepository
import com.site.pochak.app.core.data.repository.FollowRepositoryImpl
import com.site.pochak.app.core.data.repository.LikeRepository
import com.site.pochak.app.core.data.repository.LikeRepositoryImpl
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.data.repository.LoginRepositoryImpl
import com.site.pochak.app.core.data.repository.MemoriesRepository
import com.site.pochak.app.core.data.repository.MemoriesRepositoryImpl
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.data.repository.PostRepositoryImpl
import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.data.repository.ProfileRepositoryImpl
import com.site.pochak.app.core.data.repository.ReportRepository
import com.site.pochak.app.core.data.repository.ReportRepositoryImpl
import com.site.pochak.app.core.data.repository.SearchRepository
import com.site.pochak.app.core.data.repository.SearchRepositoryImpl
import com.site.pochak.app.core.data.repository.TagRepository
import com.site.pochak.app.core.data.repository.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindAlarmRepository(
        alarmRepositoryImpl: AlarmRepositoryImpl
    ): AlarmRepository

    @Binds
    abstract fun bindBlockRepository(
        blockRepositoryImpl: BlockRepositoryImpl
    ): BlockRepository

    @Binds
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    abstract fun bindFcmRepository(
        fcmRepositoryImpl: FcmRepositoryImpl
    ): FcmRepository

    @Binds
    abstract fun bindFollowRepository(
        followRepositoryImpl: FollowRepositoryImpl
    ): FollowRepository

    @Binds
    abstract fun bindLikeRepository(
        likeRepositoryImpl: LikeRepositoryImpl
    ): LikeRepository

    @Binds
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    abstract fun bindMemoriesRepository(
        memoriesRepositoryImpl: MemoriesRepositoryImpl
    ): MemoriesRepository

    @Binds
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Binds
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository

}