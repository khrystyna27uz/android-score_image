package com.imagescore.ui.score.di

import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.ui.score.ScorePresenter
import com.imagescore.utils.rx.RxSchedulers
import dagger.Module
import dagger.Provides

@Module
class ScoreFragmentModule {

    @Provides
    fun provideMarathonPresenter(
        rxSchedulers: RxSchedulers,
        imageScoreUseCase: ImageScoreUseCase
    ): ScorePresenter =
        ScorePresenter(rxSchedulers, imageScoreUseCase)

}