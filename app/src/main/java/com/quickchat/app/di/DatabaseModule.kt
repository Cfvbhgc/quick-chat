package com.quickchat.app.di

import android.content.Context
import androidx.room.Room
import com.quickchat.app.data.local.ChatDao
import com.quickchat.app.data.local.ChatDatabase
import com.quickchat.app.data.local.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "quickchat.db"
        ).build()
    }

    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao = database.chatDao()
}
