package com.javashitang.demo2;

import lombok.Data;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/*
 * 获取节点数据
 */
@Data
public class ZKGetNodeData implements Watcher {

    private ZooKeeper zooKeeper = null;
    private static Stat stat = new Stat();
    public static final String zkServerPath = "www.erlie.cc:2181";
    public static final Integer timeout = 5000;
    private static CountDownLatch countDown = new CountDownLatch(1);

    public ZKGetNodeData() { }

    public ZKGetNodeData(String connectString) {

        try {
            zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKGetNodeData());
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
        ZKGetNodeData zkServer = new ZKGetNodeData(zkServerPath);
        /*
         * path : 节点路径
         * watch : true或者false,注册一个watch事件
         * stat : 状态
         */
        byte[] resByte = zkServer.getZooKeeper().getData("/imooc", true, stat);
        String result = new String(resByte);
        System.out.println("当前值 " + result);
        countDown.await();
    }

    @Override
    public void process(WatchedEvent event) {

        try {
            if (event.getType() == Event.EventType.NodeDataChanged) {
                ZKGetNodeData zkServer = new ZKGetNodeData(zkServerPath);
                byte[] resByte = zkServer.getZooKeeper().getData("/imooc", false, stat);
                String result = new String(resByte);
                System.out.println("更改后的值:" + result);
                System.out.println("版本号变化的version：" + stat.getVersion());
                countDown.countDown();
            } else if (event.getType() == Event.EventType.NodeCreated) {

            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {

            } else if (event.getType() == Event.EventType.NodeDeleted) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
