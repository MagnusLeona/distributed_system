package magnus.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    public static ReentrantLock lock = new ReentrantLock(true);

    public static void main(String[] args) {
        Thread[] threads = new Thread[5];
        Runnable runnable = () -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " executed");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };

        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(runnable, String.format("T %s", i ));
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }
}
