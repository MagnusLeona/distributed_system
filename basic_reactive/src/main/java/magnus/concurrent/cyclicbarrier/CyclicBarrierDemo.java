package magnus.concurrent.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierDemo {
    static CyclicBarrier cyclicBarrier = new CyclicBarrier(3, ()-> {
        System.out.println("jobs done");
    });

    public static void main(String[] args) {
        Runnable r = () -> {
            try {
                Thread.sleep(1000);
                System.out.println(String.format("%s reachs the barrier", Thread.currentThread().getName()));
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(r);
        executorService.execute(r);
        executorService.execute(r);
        executorService.shutdown();
    }
}
