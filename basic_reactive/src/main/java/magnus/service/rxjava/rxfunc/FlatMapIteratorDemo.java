package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import magnus.service.rxjava.animal.Animal;
import magnus.service.rxjava.animal.Zoo;

public class FlatMapIteratorDemo {
    public static void main(String[] args) {

        Observable.fromIterable(Zoo.ZooBuilder())
                .flatMapIterable(zoo -> {
                    return zoo.animals;
                }).subscribe(new Observer<Animal>() {
                    @Override
                    public void onSubscribe(Disposable d) {

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

                    }
                });
    }
}