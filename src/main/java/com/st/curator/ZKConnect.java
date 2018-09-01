package com.st.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ZKConnect implements Watcher {

    public static final String zkServerPath = "www.erlie.cc:2181";
    public static final Integer timeout = 5000;

    public static void main(String[] args) throws Exception {

        ZooKeeper zk = new ZooKeeper(zkServerPath, timeout, new ZKConnect());
        log.warn("链接状态 {}", zk.getState());
        TimeUnit.SECONDS.sleep(2);
        log.warn("链接状态 {}", zk.getState());
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        log.warn("接收到watch通知：{}", watchedEvent);
    }
}
