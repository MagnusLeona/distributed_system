package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import magnus.service.rxjava.animal.Animal;
import magnus.service.rxjava.animal.Zoo;

public class FlatMapDemo {

    public static void main(String[] args) {
        Observable.fromArray(Zoo.ZooBuilder().toArray(new Zoo[0])).flatMap(zoo -> {
            return Observable.fromArray(zoo.animals.toArray(new Animal[0]));
        }).subscribe(new Observer<Animal>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("======================");
            }

            @Override
            public void onNext(Animal animal) {
                System.out.println("FlattedAnimals: " + "name: " + animal.name + "  age: " + animal.age + "  type: " + animal.type);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("======================");
            }
        });
    }
}
