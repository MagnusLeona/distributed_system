package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import magnus.service.rxjava.animal.Animal;
import magnus.service.rxjava.animal.Zoo;

import java.util.stream.Collectors;

public class SwitchMapDemo {
    public static void main(String[] args) {
        Observable.fromIterable(Zoo.ZooBuilder()).switchMap(zoo -> Observable.fromIterable(zoo.animals)).subscribe(new Observer<>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("-----------");
            }

            @Override
            public void onNext(Animal animal) {
                System.out.println(animal.name);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("-----------");
            }
        });
    }
}
