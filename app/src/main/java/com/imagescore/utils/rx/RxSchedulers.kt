package com.imagescore.utils.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class RxSchedulers {

    open fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
    open fun io() = Schedulers.io()
}