package com.shuhart.testlook.modules.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter : Presenter {
    private val compositeDisposable = CompositeDisposable()

    protected fun subscribe(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.add(it)
        }
    }

    protected fun unsubscribe(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.remove(it)
        }
    }

    protected fun clearSubscriptions() {
        compositeDisposable.clear()
    }

    override fun onCreate() {}

    override fun onDestroy() {
        compositeDisposable.clear()
    }
}