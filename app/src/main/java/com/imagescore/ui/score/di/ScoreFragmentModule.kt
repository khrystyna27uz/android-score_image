package com.imagescore.ui.score.di

import com.imagescore.domain.usecase.ImageScoreUseCase
import com.imagescore.ui.score.ScorePresenter
import com.imagescore.utils.rx.RxSchedulers
import dagger.Module
import dagger.Provides

@Module
class ScoreFragmentModule {

    @Provides
    fun provideMarathonPresenter(
        rxSchedulers: RxSchedulers,
        imageScoreUseCase: ImageScoreUseCase): ScorePresenter =
        ScorePresenter(rxSchedulers, imageScoreUseCase)

}