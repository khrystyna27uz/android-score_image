package com.imagescore.ui.details.di

import com.imagescore.domain.ui.score.di.ImageScoreUseCaseModule
import com.imagescore.ui.details.view.DetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DetailsFragmentBinder {

    @ContributesAndroidInjector(
        modules = [DetailsFragmentModule::class,
            ImageScoreUseCaseModule::class]
    )
    abstract fun bindDetailsFragment(): DetailsFragment
}