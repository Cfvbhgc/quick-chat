package com.quickchat.app.di

import com.quickchat.app.data.remote.ChatWebSocket
import com.quickchat.app.data.remote.MockChatWebSocket
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindChatWebSocket(impl: MockChatWebSocket): ChatWebSocket
}
