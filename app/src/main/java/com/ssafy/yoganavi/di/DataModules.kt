package com.ssafy.yoganavi.di

import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.repository.InfoRepositoryImpl
import com.ssafy.yoganavi.data.repository.LectureRepository
import com.ssafy.yoganavi.data.repository.LectureRepositoryImpl
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.repository.UserRepositoryImpl
import com.ssafy.yoganavi.data.source.info.InfoDataSource
import com.ssafy.yoganavi.data.source.info.InfoDataSourceImpl
import com.ssafy.yoganavi.data.source.user.UserDataSource
import com.ssafy.yoganavi.data.source.user.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindInfoRepository(infoRepositoryImpl: InfoRepositoryImpl): InfoRepository

    @Singleton
    @Binds
    abstract fun bindLectureRepository(lectureRepositoryImpl: LectureRepositoryImpl): LectureRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Singleton
    @Binds
    abstract fun bindInfoDataSource(infoDataSourceImpl: InfoDataSourceImpl): InfoDataSource
}
