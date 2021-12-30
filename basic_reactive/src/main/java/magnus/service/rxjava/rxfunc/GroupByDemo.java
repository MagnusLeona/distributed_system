package magnus.service.rxjava.rxfunc;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import magnus.service.rxjava.animal.Zoo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GroupByDemo {
    public static void main(String[] args) {
        Observable<GroupedObservable<String, Zoo>> groupedObservableObservable = Observable.fromArray(Zoo.ZooBuilder().toArray(new Zoo[0]))
                .groupBy(zoo -> {
                    return zoo.getName();
                });

        Observable<Zoo> concat = Observable.concat(groupedObservableObservable);
//        Observable<Zoo> merge = Observable.merge(groupedObservableObservable);
        concat.subscribe(new Observer<Zoo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Zoo zoo) {
                System.out.println(zoo.getName() + zoo.hashCode());
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
