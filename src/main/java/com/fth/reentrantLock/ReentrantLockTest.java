package com.fth.reentrantLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author FTH
 * @Title
 * @Description
 * @Copyright: Copyright (c) 2020
 * @Company: morelean
 * @since 2020-7-29
 */
public class ReentrantLockTest {

    public static void main(String[] args) {
        final ReentrantLock rl = new ReentrantLock();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " run ");
                   // synchronized (rl){
                     boolean lock = false;
                        try{
                            //lock = rl.tryLock(3, TimeUnit.SECONDS);
                           // rl.lock();//不可打断
                            rl.lockInterruptibly();//可打断
//                            if(!lock){
//                                System.out.println(Thread.currentThread().getName() + " lock " +lock);
//                                return ;
//                            }
                            for (int j = 0; ; j++) {
                                System.out.println(Thread.currentThread().getName() + " lock ");
                                TimeUnit.SECONDS.sleep(1);
                                if(j == 2){
                                    //Thread.currentThread().interrupt();
                                    //break;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //if(lock){
                                rl.unlock();
                            //}

                            System.out.println(Thread.currentThread().getName() + " end ");
                        }
                    }

               // }
            });
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
            threads[2].interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
