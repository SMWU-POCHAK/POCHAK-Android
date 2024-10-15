package com.site.pochak.app.core.data.model.di

import com.site.pochak.app.core.data.model.repository.LoginRepository
import com.site.pochak.app.core.data.model.repository.LoginRepositoryImpl
import com.site.pochak.app.core.data.model.repository.ProfileRepository
import com.site.pochak.app.core.data.model.repository.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}