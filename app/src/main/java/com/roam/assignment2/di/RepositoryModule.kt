package com.roam.assignment2.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.roam.assignment2.data.repository.AuthRepository
import com.roam.assignment2.data.repository.AuthRepositoryImpl
import com.roam.assignment2.data.repository.DashboardRepository
import com.roam.assignment2.data.repository.DashboardRepositoryImpl

/** Binds each repository interface to its implementation so consumers depend on abstractions only. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}