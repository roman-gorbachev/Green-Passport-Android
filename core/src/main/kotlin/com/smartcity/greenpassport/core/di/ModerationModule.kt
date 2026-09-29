package com.smartcity.greenpassport.core.di

import com.smartcity.greenpassport.core.moderation.TextModerator
import com.smartcity.greenpassport.core.moderation.WordListTextModerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ModerationModule {
    @Binds
    fun bindTextModerator(impl: WordListTextModerator): TextModerator
}
