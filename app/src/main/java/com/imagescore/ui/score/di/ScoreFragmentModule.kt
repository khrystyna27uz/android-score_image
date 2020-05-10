package com.imagescore.ui.score.di

import android.content.Context
import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.ui.score.ScorePresenter
import com.imagescore.utils.rx.RxSchedulers
import dagger.Module
import dagger.Provides

@Module
class ScoreFragmentModule {

    @Provides
    fun provideMarathonPresenter(
        context: Context,
        rxSchedulers: RxSchedulers,
        imageScoreUseCase: ImageScoreUseCase
    ) = ScorePresenter(context, rxSchedulers, imageScoreUseCase)

}