package com.imagescore.ui.main.di

import com.imagescore.ui.main.view.MainActivity
import com.imagescore.ui.score.di.ScoreFragmentBinder
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityBinder {

    @ContributesAndroidInjector(
        modules = [
            MainActivityModule::class,
            ScoreFragmentBinder::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity
}