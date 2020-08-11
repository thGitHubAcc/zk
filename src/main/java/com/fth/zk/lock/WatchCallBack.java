package com.fth.zk.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author FTH
 * @Title
 * @Description
 * @Copyright: Copyright (c) 2020
 * @Company: morelean
 * @since 2020-7-24
 */
public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    ZooKeeper zk ;
    String threadName;
    CountDownLatch cc = new CountDownLatch(1);
    String pathName;


    public void tryLock(){
        try {
            System.out.println(threadName + " creating...");
            zk.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,this,"ctx");
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void unLock(){
        try {
            zk.delete(pathName,-1);
            System.out.println(threadName + " over work....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * create StringCallback
     * @param rc
     * @param path
     * @param ctx
     * @param name
     */
    public void processResult(int rc, String path, Object ctx, String name) {
        if(name != null){
            System.out.println(threadName + " create node : " + name);
            pathName = name;
            zk.getChildren("/",false,this,"children ctx");
        }
    }

    /**
     * getchildren callback
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        //排序
        Collections.sort(children);
        int index = children.indexOf(pathName.substring(1));
//        System.out.println(pathName+":"+index);
        //当前是否是第一个节点(即拥有锁的节点)
        if(index == 0){
            try {
                System.out.println(threadName +" i am first....");
                zk.setData("/",threadName.getBytes(),stat.getVersion());
                cc.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            zk.exists("/"+children.get(index - 1),this,this,"exist ctx");
        }

    }

    /**
     * Watcher
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        System.out.println("watch:"+watchedEvent);
    }

    public void processResult(int rc, String s, Object ctx, Stat stat) {
        System.out.println("i = " + rc + ", s = " + s + ", ctx = " + ctx + ", stat = " + stat);
    }


    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public CountDownLatch getCc() {
        return cc;
    }

    public void setCc(CountDownLatch cc) {
        this.cc = cc;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }



}
