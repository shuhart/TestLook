package com.shuhart.testlook.business.network;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RxThreadCallAdapter extends CallAdapter.Factory {

    private RxJava2CallAdapterFactory rxFactory = RxJava2CallAdapterFactory.create();
    private Scheduler subscribeScheduler;
    private Scheduler observerScheduler;

    public RxThreadCallAdapter(Scheduler subscribeScheduler, Scheduler observerScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observerScheduler = observerScheduler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        CallAdapter<?, ?> callAdapter = rxFactory.get(returnType, annotations, retrofit);
        return new ThreadCallAdapter(callAdapter);
    }

    final class ThreadCallAdapter<R> implements CallAdapter<R, Object> {
        CallAdapter<R, Object> delegateAdapter;

        ThreadCallAdapter(CallAdapter<R, Object> delegateAdapter) {
            this.delegateAdapter = delegateAdapter;
        }

        @Override
        public Type responseType() {
            return delegateAdapter.responseType();
        }

        @Override
        public Object adapt(@NonNull Call<R> call) {
            return ((Observable<?>) delegateAdapter.adapt(call))
                    .subscribeOn(subscribeScheduler)
                    .observeOn(observerScheduler);
        }
    }
}