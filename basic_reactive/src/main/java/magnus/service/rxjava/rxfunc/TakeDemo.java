package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import magnus.service.rxjava.animal.Zoo;

public class TakeDemo {
    public static void main(String[] args) {
        Observable.fromIterable(Zoo.ZooBuilder()).take(3).subscribe(new Observer<Zoo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Zoo zoo) {
                System.out.println(zoo.getName());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
