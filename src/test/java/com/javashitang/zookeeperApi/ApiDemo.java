package com.javashitang.zookeeperApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author lilimin
 * @since 2020-11-15
 */
public class ApiDemo {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    public class WatcherImpl implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            if (Event.KeeperState.SyncConnected == event.getState()) {
                countDownLatch.countDown();
            }
            if (Event.EventType.NodeDataChanged == event.getType()) {

            } else if (Event.EventType.NodeCreated == event.getType()) {

            } else if (Event.EventType.NodeChildrenChanged == event.getType()) {

            } else if (Event.EventType.NodeDeleted == event.getType()) {

            }

        }
    }

    @Test
    @Before
    public void connect() throws Exception {
        String connectString = "myhost:2181";
        zooKeeper = new ZooKeeper(connectString, 5000, new WatcherImpl());
        countDownLatch.await();
        System.out.println("zookeeper connected");
    }

    @Test
    public void create() throws Exception {
        // 创建临时节点
        zooKeeper.create("/java", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    @Test
    public void getData() throws Exception {
        byte[] bytes = zooKeeper.getData("/java", true, new Stat());
        String data = new String(bytes);
        System.out.println(data);
    }

    @Test
    public void setData() {
//        zooKeeper.setData();
    }

    @Test
    public void delete() {
//        zooKeeper.delete();
    }
}
