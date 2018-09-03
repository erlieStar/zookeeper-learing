package com.st.javaapi.demo1;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZooKeeper_Create_API_Sync_Usage implements Watcher {

    public static final String zkServerPath = "www.erlie.cc:2181";
    public static final Integer timeout = 5000;
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zk = new ZooKeeper(zkServerPath, timeout, new ZooKeeper_Create_API_Sync_Usage());
        connectedSemaphore.await();
        List<ACL> acls = new ArrayList<>();

    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
