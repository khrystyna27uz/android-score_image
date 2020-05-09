package com.imagescore.ui.score.di

import com.imagescore.domain.ui.score.di.ImageScoreUseCaseModule
import com.imagescore.ui.score.view.ScoreFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScoreFragmentBinder {

    @ContributesAndroidInjector(
        modules = [ScoreFragmentModule::class,
            ImageScoreUseCaseModule::class]
    )
    abstract fun bindScoreFragment(): ScoreFragment
}