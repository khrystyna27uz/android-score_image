package com.imagescore.ui.main.view

import androidx.fragment.app.Fragment

interface Navigation {
    fun navigate(fragment: Fragment, resetBackStack: Boolean = false)

    fun navigateBack()
}