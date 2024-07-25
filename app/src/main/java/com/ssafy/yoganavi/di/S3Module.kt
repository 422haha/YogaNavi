package com.ssafy.yoganavi.di

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object S3Module {

    @Singleton
    @Provides
    fun provideCredentials(): BasicAWSCredentials = BasicAWSCredentials(
        BuildConfig.ACCESS_KEY,
        BuildConfig.SECRET_KEY
    )

    @Singleton
    @Provides
    fun provideRegion(): Region = Region.getRegion(Regions.AP_NORTHEAST_2)

    @Singleton
    @Provides
    fun provideS3Client(
        basicAWSCredentials: BasicAWSCredentials,
        region: Region
    ): AmazonS3Client = AmazonS3Client(basicAWSCredentials, region)

    @Singleton
    @Provides
    fun provideTransferUtility(
        @ApplicationContext context: Context,
        s3Client: AmazonS3Client
    ): TransferUtility = TransferUtility.builder()
        .context(context.applicationContext)
        .s3Client(s3Client)
        .build()

}