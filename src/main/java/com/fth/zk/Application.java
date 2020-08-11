package com.fth.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author FTH
 * @Title
 * @Description
 * @Copyright: Copyright (c) 2020
 * @Company: morelean
 * @since 2020-7-24
 */
public class Application {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        System.out.println("hello world");
        final CountDownLatch cd = new CountDownLatch(1);
        final ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 300, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                String path = watchedEvent.getPath();
                System.out.println("path:" + path);
                System.out.println("new zk watch: "+ watchedEvent.toString());
                switch (watchedEvent.getState()){
                    case Unknown:
                        System.out.println("Unknown");
                        break;
                    case Disconnected:
                        System.out.println("Disconnected");
                        break;
                    case NoSyncConnected:
                        System.out.println("NoSyncConnected");
                        break;
                    case SyncConnected:
                        System.out.println("SyncConnected");
                        cd.countDown();
                        break;
                    case AuthFailed:
                        System.out.println("AuthFailed");
                        break;
                    case ConnectedReadOnly:
                        System.out.println("ConnectedReadOnly");
                        break;
                    case SaslAuthenticated:
                        System.out.println("SaslAuthenticated");
                        break;
                    case Expired:
                        System.out.println("Expired");
                        break;
                }
            }
        });
        cd.await();

        ZooKeeper.States state = zooKeeper.getState();
        switch (state){
            case CONNECTING:
                System.out.println("CONNECTING");
                break;
            case ASSOCIATING:
                System.out.println("ASSOCIATING");
                break;
            case CONNECTED:
                System.out.println("CONNECTED");
                break;
            case CONNECTEDREADONLY:
                System.out.println("CONNECTEDREADONLY");
                break;
            case CLOSED:
                System.out.println("CLOSED");
                break;
            case AUTH_FAILED:
                System.out.println("AUTH_FAILED");
                break;
            case NOT_CONNECTED:
                System.out.println("NOT_CONNECTED");
                break;
        }

        Stat exists = zooKeeper.exists("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("exist ooxx watch:" + watchedEvent);
            }
        });
        System.out.println("exist stat : " + exists);
        if(exists != null){
            zooKeeper.delete("/ooxx",exists.getVersion());
        }

        String pathName = zooKeeper.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("create pathname:"+pathName);
        final Stat stat=new Stat();
        byte[] data = zooKeeper.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("getdata watch :" + watchedEvent);
                try {
                    zooKeeper.getData("/ooxx",this,stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println("getData : " + new String(data));
        System.out.println("getdata stat:"+stat);

        Stat setStat = zooKeeper.setData("/ooxx", "newdata".getBytes(), stat.getVersion());
        System.out.println("setdata stat:"+setStat);
        //还会触发吗？
        Stat setStat2 = zooKeeper.setData("/ooxx", "newdata01".getBytes(), setStat.getVersion());


        System.out.println("-------async start----------");

        zooKeeper.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("rc = " + rc + ", path = " + path + ", ctx = " + ctx + ", data = " + new String(data)+ ", stat = " + stat);
            }
        },"ctx");
        System.out.println("-------async over----------");

        Thread.sleep(999999999);

    }
}
