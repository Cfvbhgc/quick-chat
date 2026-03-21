package com.quickchat.app.di

import android.content.Context
import com.quickchat.app.data.repository.ChatRepository
import com.quickchat.app.data.repository.ChatRepositoryImpl
import com.quickchat.app.data.repository.UserRepository
import com.quickchat.app.data.repository.UserRepositoryImpl
import com.quickchat.app.util.ThemePreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
            return ThemePreferences(context)
        }
    }
}
