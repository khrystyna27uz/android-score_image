package com.imagescore.utils.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class RxSchedulers {

    open fun mainThread() = AndroidSchedulers.mainThread()
    open fun io() = Schedulers.io()
    open fun computation() = Schedulers.computation()
    fun trampoline() = Schedulers.trampoline()
}