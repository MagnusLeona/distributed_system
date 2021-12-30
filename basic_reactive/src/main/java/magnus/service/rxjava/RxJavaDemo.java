package magnus.service.rxjava;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import magnus.service.rxjava.animal.Animal;
import magnus.service.rxjava.animal.Zoo;

import java.util.ArrayList;
import java.util.Arrays;

public class RxJavaDemo {
    public static void main(String[] args) {
        // Observable订阅了Observer。Observable对象执行的任何操作，将通知到Observer中。
        // return的对象将被onNext接收到？
        Observable.fromArray(Zoo.ZooBuilder().toArray(new Zoo[0])).flatMap(new Function<Zoo, ObservableSource<Animal>>() {
            @Override
            public ObservableSource<Animal> apply(Zoo zoo) throws Exception {
                return Observable.fromArray(zoo.animals.toArray(new Animal[0]));
            }
        }).subscribe(new Observer<Animal>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Animal animal) {
                System.out.println("name: " + animal.name + " age: " + animal.age + " type: " + animal.type);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("OnComplete");
            }
        });

//        Observable.fromArray(1, 2, 3, 4, 5, 6).map((integer) -> {
//            return "this is " + integer;
//        }).subscribe(new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                System.out.println("OnSubscribe");
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.println("OnNext : " + s);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println("OnError");
//            }
//
//            @Override
//            public void onComplete() {
//                System.out.println("OnComplete");
//            }
//        });
    }
}
