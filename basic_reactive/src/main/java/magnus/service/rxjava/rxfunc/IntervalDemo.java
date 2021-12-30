package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class IntervalDemo {
    public static void main(String[] args) {
        String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g"};
        Observable.just(0,1,2,3,4,5)
                .interval(300, TimeUnit.MILLISECONDS)
                .map(aLong -> letters[aLong.intValue()])
                .take(letters.length)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

//        Observable<Long> take1 = Observable.just(0,1,2,3,4,5).interval(500, TimeUnit.MILLISECONDS).take(5);

//        Observable.merge(take, take1).subscribe(new Observer<Serializable>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Serializable serializable) {
//                System.out.println(serializable.toString());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }
}
