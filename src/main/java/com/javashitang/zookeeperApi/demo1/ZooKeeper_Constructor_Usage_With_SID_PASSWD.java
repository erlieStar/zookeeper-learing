package com.javashitang.zookeeperApi.demo1;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {

    public static final String zkServerPath = "www.erlie.cc:2181";
    public static final Integer timeout = 5000;
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zk = new ZooKeeper(zkServerPath, timeout, new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
        connectedSemaphore.await();
        long sessionId = zk.getSessionId();
        byte[] passwd = zk.getSessionPasswd();

        // 第一次使用了错误的sessionId和sessionPasswd，接收到expired事件通知
        zk = new ZooKeeper(zkServerPath, timeout, new ZooKeeper_Constructor_Usage_With_SID_PASSWD(), 1L, "test".getBytes());
        zk = new ZooKeeper(zkServerPath, timeout, new ZooKeeper_Constructor_Usage_With_SID_PASSWD(), sessionId, passwd);
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
