package com.imagescore.mvp

import java.lang.ref.WeakReference

abstract class BasicPresenter< V : BasicView> {

    private var view: WeakReference<V>? = null

    private fun bindView(view: V) {
        this.view = WeakReference(view)
    }

    private fun unbindView() {
        this.view = null
    }

    protected open fun onEnterScope() {}

    protected open fun onExitScope() {}

    protected fun getView(): V? {
        return view?.get()
    }

    fun enterWithView(v: V) {
        bindView(v)
        onEnterScope()
    }

    fun exitFromView() {
        onExitScope()
        unbindView()
    }
}