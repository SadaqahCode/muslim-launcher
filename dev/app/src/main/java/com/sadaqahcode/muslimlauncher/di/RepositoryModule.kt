package com.sadaqahcode.muslimlauncher.di

import com.sadaqahcode.muslimlauncher.data.repository.ChallengeRepositoryImpl
import com.sadaqahcode.muslimlauncher.data.repository.DhikrRepositoryImpl
import com.sadaqahcode.muslimlauncher.domain.repository.ChallengeRepository
import com.sadaqahcode.muslimlauncher.domain.repository.DhikrRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChallengeRepository(
        impl: ChallengeRepositoryImpl
    ): ChallengeRepository

    @Binds
    @Singleton
    abstract fun bindDhikrRepository(
        impl: DhikrRepositoryImpl
    ): DhikrRepository
}