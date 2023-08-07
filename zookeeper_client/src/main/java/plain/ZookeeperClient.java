package plain;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZookeeperClient {

    public static void main(String[] args) throws InterruptedException {
        ZooKeeper zooKeeper = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper("124.222.129.114:2181", 4000, watchedEvent -> {
                if (watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            ZooKeeper.States state = zooKeeper.getState();
            System.out.println(state.isConnected());
            CountDownLatch countDownLatch1 = new CountDownLatch(2);

            byte[] data1 = zooKeeper.getData("/magnus/child", false, null);
            System.out.println("first get data: " + new String(data1));

            byte[] data = zooKeeper.getData("/magnus/child", watchedEvent -> {
                // 一次性的watcher,只会触发一次
                System.out.println("this is a watched event" + watchedEvent.getPath());
                countDownLatch1.countDown();
            }, new Stat());
            countDownLatch1.await();
            System.out.println("data: " + new String(data));
            System.out.println("connecting zookeeper successfully");
        } catch (Exception ignored) {

        } finally {
            assert zooKeeper != null;
        }
    }
}
