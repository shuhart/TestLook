package com.shuhart.testlook.modules.base

import android.os.Bundle
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

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun onSaveInstanceState(bundle: Bundle) {}

    override fun onCreate(bundle: Bundle?) {}
}