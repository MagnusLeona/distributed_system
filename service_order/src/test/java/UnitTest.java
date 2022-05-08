import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UnitTest {

    public void test() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, Thread::new, new ThreadPoolExecutor.AbortPolicy());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> System.out.println("abc"), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        UnitTest unitTest = new UnitTest();
        unitTest.test();
    }
}
