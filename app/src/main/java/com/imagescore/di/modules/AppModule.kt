package com.imagescore.di.modules

import android.app.Application
import android.content.Context
import com.imagescore.mvp.BasicPresenter
import com.imagescore.mvp.BasicView
import com.imagescore.utils.rx.RxSchedulers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    private var mainGeneral: BasicPresenter<BasicView>? = null

    @Suppress("UNCHECKED_CAST")
    private fun <T> oncePerView(newPresenter: T): T {
        return if (mainGeneral != null) {
            mainGeneral as T
        } else {
            mainGeneral = newPresenter as BasicPresenter<BasicView>
            newPresenter
        }
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideSchedulers(): RxSchedulers = RxSchedulers()

}