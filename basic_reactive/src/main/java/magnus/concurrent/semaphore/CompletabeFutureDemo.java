package magnus.concurrent.semaphore;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletabeFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()-> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "aaa";
        });

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(()-> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "bbb";
        });

        System.out.println("ccc");
        System.out.println(completableFuture1.get());
        System.out.println(completableFuture.get());
    }
}
