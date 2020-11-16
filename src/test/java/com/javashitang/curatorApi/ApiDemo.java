package com.javashitang.curatorApi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lilimin
 * @since 2020-10-25
 */
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

    @Test
    public void create() throws Exception {
        // 创建一个内容为空的节点
        client.create().forPath("/noContent");
        // 创建包含内容的节点
        client.create().forPath("/content", "我是内容".getBytes());
        // 创建临时节点，并递归创建父节点，在递归创建父节点时，父节点为持久节点
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/curator/content", "递归".getBytes());
    }

    @Test
    public void getData() throws Exception {
        client.getData().forPath("/noContent");
    }

    @Test
    public void setData() throws Exception {
        client.setData().forPath("/content", "新内容".getBytes());
    }

    @Test
    public void delete() throws Exception {
        client.delete().forPath("/noContent");
        // 递归删除子节点
        client.delete().deletingChildrenIfNeeded().forPath("/curator/content");
    }

    @Test
    public void nodeCacheListener() throws Exception {
    }

    /**
     * 事件监听
     * https://www.cnblogs.com/crazymakercircle/p/10228385.html
     */
    @Test
    public void pathChildrenCacheListener() throws Exception {
        // cacheData 为 true，接收到列表变更事件的同时，也会获得节点内容
        PathChildrenCache cache = new PathChildrenCache(client, "watcher", true);
        PathChildrenCacheListener listener = ((CuratorFramework client, PathChildrenCacheEvent event) -> {
            ChildData data = event.getData();
            switch (event.getType()) {
                case CHILD_ADDED:
                    break;
                case CHILD_UPDATED:
                    break;
                case CHILD_REMOVED:
                    break;
                default:
                    break;
            }
        });
        cache.getListenable().addListener(listener);
        cache.start();
    }
}
