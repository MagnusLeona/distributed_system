package magnus.leona.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ZookeeperTemplate {

    @Autowired
    ZooKeeper zooKeeper;

    /**
     * 判断指定节点是否存在
     *
     * @param path
     * @param needWatch 指定是否复用zookeeper中的默认watcher
     * @return
     */
    public Stat exists(String path, boolean needWatch) {
        Stat exists = null;
        try {
            exists = zooKeeper.exists(path, needWatch);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 判断指定节点是否存在，并传入watcher监听事件。三种监听类型：创建、删除、更新
     *
     * @param path
     * @param watcher
     * @return
     */
    public Stat exists(String path, Watcher watcher) {
        Stat exists = null;
        try {
            exists = zooKeeper.exists(path, watcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 创建持久化节点
     *
     * @param path
     * @param data
     * @return
     */
    public boolean createNode(String path, String data) {
        try {
            zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新节点内容
     *
     * @param path
     * @param data
     * @return
     */
    public boolean updateNode(String path, String data) {
        try {
            // version参数表示需要修改的数据的版本。如果数据的版本和真是版本不同，则更新失败。如果设置为-1，则表示忽略版本检查。
            // zk的数据版本从0开始计数，如果客户端传入-1，则表示zk服务器需要基于最新的数据进行更新，如果对zk的数据节点更新没有原子性要求，则可以使用-1来表示版本。
            zooKeeper.setData(path, data.getBytes(), -1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除节点
     *
     * @param path
     * @return
     */
    public boolean deleteNode(String path) {
        try {
            zooKeeper.delete(path, -1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取子节点（不包含孙节点）
     *
     * @param path
     * @return
     */
    public List<String> getChildren(String path) {
        try {
            List<String> children = zooKeeper.getChildren(path, false);
            return children;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回指定节点的值
     * @param path
     * @param watcher
     * @return
     */
    public String getData(String path, Watcher watcher) {
        try {
            Stat stat = new Stat();
            byte[] data = zooKeeper.getData(path, watcher, stat);
            return new String(data);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
