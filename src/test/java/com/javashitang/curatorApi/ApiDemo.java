package com.javashitang.curatorApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author lilimin
 * @since 2020-10-25
 */
@Slf4j
public class ApiDemo {

    private CuratorFramework client;

    /**
     * RetryPolicy 是重试策略接口
     * https://www.cnblogs.com/qingyunzong/p/8666288.html
     */
    @Test
    @Before
    public void connect() {
        String connectString = "myhost:2181";
        // 重试3次，每次间隔1000ms
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        client.start();
    }

    /**
     * 创建一个持久节点
     */
    @Test
    public void createPersistent() throws Exception {
        // 创建一个内容为空的节点
        client.create().forPath("/persistent");
        // 创建包含内容的节点
        client.create().forPath("/persistentContent", "我是内容".getBytes());
    }

    /**
     * 创建临时节点
     */
    @Test
    public void createEphemeral() throws Exception {
        // 创建一个内容为空的节点
        client.create().withMode(CreateMode.EPHEMERAL).forPath("Ephemeral");
        // 创建包含内容的节点
        client.create().withMode(CreateMode.EPHEMERAL).forPath("/ephemeralContent", "我是内容".getBytes());
    }

    /**
     * 获取值
     */
    @Test
    public void getData() throws Exception {
        client.getData().forPath("/persistentContent");
    }

    /**
     * 更新值
     */
    @Test
    public void setData() throws Exception {
        client.setData().forPath("/persistentContent", "新内容".getBytes());
    }

    /**
     * 删除
     */
    @Test
    public void delete() throws Exception {
        client.delete().forPath("/persistent");
    }

    /**
     * 检测节点是否存在
     */
    @Test
    public void checkExists() throws Exception {
        Stat stat = client.checkExists().forPath("/persistent");
        if (stat == null) {
            System.out.println("节点不存在");
        } else {
            System.out.println("节点存在");
        }
    }

    @Test
    public void watcher() throws Exception {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 只输出一次
                // /watchDemo SyncConnected NodeDataChanged
                System.out.println(event.getPath() + " " + event.getState() + " " + event.getType());
            }
        };

        String path = "/watchDemo";
        if (client.checkExists().forPath(path) == null) {
            client.create().forPath(path);
        }
        client.getData().usingWatcher(watcher).forPath(path);

        client.setData().forPath(path, "第一个变更的内容".getBytes());
        client.setData().forPath(path, "第二个变更的内容".getBytes());

        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void curatorWatcher() throws Exception {
        CuratorWatcher watcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                // 只输出一次
                // /watchDemo SyncConnected NodeDataChanged
                System.out.println(event.getPath() + " " + event.getState() + " " + event.getType());
            }
        };

        String path = "/watchDemo";
        if (client.checkExists().forPath(path) == null) {
            client.create().forPath(path);
        }
        client.getData().usingWatcher(watcher).forPath(path);

        client.setData().forPath(path, "第一个变更的内容".getBytes());
        client.setData().forPath(path, "第二个变更的内容".getBytes());

        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 事件监听
     * https://www.cnblogs.com/crazymakercircle/p/10228385.html
     */
    @Test
    public void treeCacheListener() throws Exception {

        String bossPath = "/treeCache";
        String workerPath = "/treeCache/id-";

        if (client.checkExists().forPath(bossPath) == null) {
            client.create().forPath(bossPath);
        }

        TreeCache treeCache = new TreeCache(client, bossPath);
        TreeCacheListener listener = ((CuratorFramework client, TreeCacheEvent event) -> {
            String path = null;
            String content = null;
            switch (event.getType()) {
                case NODE_ADDED:
                    log.info("节点增加");
                    path = event.getData().getPath();
                    content = new String(event.getData().getData());
                    break;
                case NODE_UPDATED:
                    log.info("节点更新");
                    path = event.getData().getPath();
                    content = new String(event.getData().getData());
                    break;
                case NODE_REMOVED:
                    log.info("节点移除");
                    path = event.getData().getPath();
                    content = new String(event.getData().getData());
                    break;
                default:
                    break;
            }
            // 事件类型为: NODE_ADDED, 路径为: /treeCache, 内容为: 192.168.97.69
            // 事件类型为: INITIALIZED, 路径为: null, 内容为: null
            // 事件类型为: NODE_ADDED, 路径为: /treeCache/id-0, 内容为: 0
            // 事件类型为: NODE_ADDED, 路径为: /treeCache/id-1, 内容为: 1
            // 事件类型为: NODE_REMOVED, 路径为: /treeCache/id-0, 内容为: 0
            // 事件类型为: NODE_REMOVED, 路径为: /treeCache/id-1, 内容为: 1
            // 事件类型为: NODE_REMOVED, 路径为: /treeCache, 内容为: 192.168.97.69
            log.info("事件类型为: {}, 路径为: {}, 内容为: {}", event.getType(), path, content);
        });
        treeCache.getListenable().addListener(listener);
        treeCache.start();

        // 创建2个子节点
        for (int i = 0; i < 2; i++) {
            client.create().forPath(workerPath + i, String.valueOf(i).getBytes());
        }

        // 删除2个子节点
        for (int i = 0; i < 2; i++) {
            client.delete().forPath(workerPath + i);
        }

        // 删除当前节点
        client.delete().forPath(bossPath);

        TimeUnit.SECONDS.sleep(3);
    }
}
