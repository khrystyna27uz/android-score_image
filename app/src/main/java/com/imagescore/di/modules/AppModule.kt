package com.imagescore.di.modules

import android.app.Application
import android.content.Context
import com.imagescore.utils.rx.RxSchedulers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideSchedulers(): RxSchedulers = RxSchedulers()

}