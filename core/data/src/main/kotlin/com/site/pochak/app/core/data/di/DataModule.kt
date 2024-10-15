package com.site.pochak.app.core.data.di

import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.data.repository.LoginRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsLoginRepository(
        loginRepository: LoginRepositoryImpl,
    ): LoginRepository
}