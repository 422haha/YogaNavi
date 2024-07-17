package com.ssafy.yoganavi.di

import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.repository.UserRepositoryImpl
import com.ssafy.yoganavi.data.source.user.UserDataSource
import com.ssafy.yoganavi.data.source.user.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource
}
