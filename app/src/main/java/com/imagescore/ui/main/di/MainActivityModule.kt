package com.imagescore.ui.main.di

import com.imagescore.ui.main.MainPresenter
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun provideMainPresenter():
            MainPresenter =
        MainPresenter()
}