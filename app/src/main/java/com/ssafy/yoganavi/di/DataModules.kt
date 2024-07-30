package com.ssafy.yoganavi.di

import com.ssafy.yoganavi.data.repository.ai.PoseRepository
import com.ssafy.yoganavi.data.repository.ai.PoseRepositoryImpl
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.info.InfoRepositoryImpl
import com.ssafy.yoganavi.data.repository.lecture.LectureRepository
import com.ssafy.yoganavi.data.repository.lecture.LectureRepositoryImpl
import com.ssafy.yoganavi.data.repository.user.UserRepository
import com.ssafy.yoganavi.data.repository.user.UserRepositoryImpl
import com.ssafy.yoganavi.data.source.ai.PoseDataSource
import com.ssafy.yoganavi.data.source.ai.PoseDataSourceImpl
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

    @Singleton
    @Binds
    abstract fun bindPoseRepository(poseRepositoryImpl: PoseRepositoryImpl): PoseRepository
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

    @Singleton
    @Binds
    abstract fun bindPoseDataSource(poseDataSourceImpl: PoseDataSourceImpl): PoseDataSource
}
