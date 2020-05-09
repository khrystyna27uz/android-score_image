package com.imagescore.ui.details.di

import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.ui.details.DetailsPresenter
import com.imagescore.utils.rx.RxSchedulers
import dagger.Module
import dagger.Provides

@Module
class DetailsFragmentModule {

    @Provides
    fun provideDetailsPresenter(
        rxSchedulers: RxSchedulers,
        imageScoreUseCase: ImageScoreUseCase
    ): DetailsPresenter =
        DetailsPresenter(rxSchedulers, imageScoreUseCase)

}