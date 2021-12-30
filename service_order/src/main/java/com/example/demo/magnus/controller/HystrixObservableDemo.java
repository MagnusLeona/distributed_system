package com.example.demo.magnus.controller;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class HystrixObservableDemo extends HystrixObservableCommand<String> {

    public String name;

    public HystrixObservableDemo(String string) {
        super(HystrixCommandGroupKey.Factory.asKey("helloword"));
        this.name = name;
    }


    @Override
    protected Observable<String> construct() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext("Hello---");
                        subscriber.onNext(name + "");
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static void main(String[] args) {
        HystrixObservableDemo hystrixObservableDemo = new HystrixObservableDemo("Magnus");
        Observable<String> construct = hystrixObservableDemo.construct();
    }
}
