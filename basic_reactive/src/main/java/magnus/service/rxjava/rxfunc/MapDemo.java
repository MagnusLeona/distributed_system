package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import magnus.service.rxjava.animal.Zoo;

import java.util.stream.Collectors;

public class MapDemo {
    public static void main(String[] args) {
        Observable.fromArray(Zoo.ZooBuilder().toArray(new Zoo[0])).map(zoo -> {
            return zoo.animals.stream().map(animal -> animal.name).collect(Collectors.joining("-"));
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("==========");
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
                System.out.println("==========");

            }
        });
    }
}
