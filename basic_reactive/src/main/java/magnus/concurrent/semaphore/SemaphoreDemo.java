package magnus.concurrent.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    static Semaphore semaphore = new Semaphore(2);

    public static void main(String[] args) {
        Runnable runnable = ()-> {
            try {
                if(semaphore.availablePermits() == 0) {
                    System.out.println("blocked");
                }
                semaphore.acquire();
                System.out.println("acquired");
                Thread.sleep(1000);
                System.out.println("released");
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.execute(runnable);
        executorService.execute(runnable);
        executorService.execute(runnable);
        executorService.execute(runnable);
        executorService.execute(runnable);
        executorService.shutdown();
    }
}
