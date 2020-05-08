package com.imagescore.ui.main

import com.imagescore.mvp.BasicPresenter
import com.imagescore.ui.main.view.MainView

class MainPresenter : BasicPresenter<MainView>() {

    override fun onEnterScope() {
        super.onEnterScope()
        getView()?.goToScoreFragment()
    }
}