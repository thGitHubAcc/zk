package com.fth.zk.lock;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author FTH
 * @Title
 * @Description
 * @Copyright: Copyright (c) 2020
 * @Company: morelean
 * @since 2020-7-24
 */
public class LockApplication {
    public static void main(String[] args) throws Exception {
        final ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 300, new WatchCallBack());



        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    WatchCallBack watchCallBack = new WatchCallBack();
                    String threadName = Thread.currentThread().getName();
                    watchCallBack.setThreadName(threadName);
                    watchCallBack.setZk(zooKeeper);

                    watchCallBack.tryLock();
                    System.out.println(threadName + ": working");
                    watchCallBack.unLock();
                }
            }.start();
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
