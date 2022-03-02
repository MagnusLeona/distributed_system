package magnus.leona.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Configuration
@PropertySource("classpath:properties/zookeeper.properties")
public class ZookeeperConfiguration {

    @Value("${zookeeper.host}")
    public String zookeeperHost;
    @Value("${zookeeper.timeout}")
    public int timeout;

    @Bean
    public ZooKeeper zooKeeper() {
        ZooKeeper zooKeeper = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            log.info("[zookeeper] 初始化连接中");
            zooKeeper = new ZooKeeper(zookeeperHost, timeout, watchedEvent -> {
               if(watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected ) {
                   countDownLatch.countDown();
               }
            });
            countDownLatch.await();
            log.info("[zookeeper] 当前连接状态={}", zooKeeper.getState());
        } catch (IOException | InterruptedException e) {
            log.info("[zookeeper] 初始化失败," + e.getMessage());
        }
        return zooKeeper;
    }
}
