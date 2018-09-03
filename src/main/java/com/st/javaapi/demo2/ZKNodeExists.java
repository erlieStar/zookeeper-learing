package com.st.javaapi.demo2;

import lombok.Data;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/*
 * 节点是否存在
 */
@Data
public class ZKNodeExists implements Watcher {

    private ZooKeeper zooKeeper = null;
    public static final String zkServerPath = "www.erlie.cc:2181";
    public static final Integer timeout = 5000;
    private static CountDownLatch countDown = new CountDownLatch(1);

    public ZKNodeExists() { }

    public ZKNodeExists(String connectString) {

        try {
            zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKNodeExists());
        } catch (IOException e) {
            e.printStackTrace();
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ZKNodeExists zkServer = new ZKNodeExists(zkServerPath);
        Stat stat = zkServer.getZooKeeper().exists("/imooc-test", true);
        if (stat != null) {
            System.out.println(stat.getVersion());
        } else {
            System.out.println("节点不存在");
        }
    }

    @Override
    public void process(WatchedEvent event) {
    }
}
