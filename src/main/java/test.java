/**
 * @author FTH
 * @Title
 * @Description
 * @Copyright: Copyright (c) 2020
 * @Company: morelean
 * @since 2020-7-28
 */
public class test {
    static Object lock = new Object();
    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            final int index = i;
            new Thread(new Runnable() {
                public void run() {
                    test t = new test();
                    t.m1("thread-"+index);
                }
            },"thread-" + i).start();

        }
        System.out.println("lock release");
    }

    public void m1(String threadName){
        synchronized (lock){
            System.out.println(threadName);
            System.out.println("lock");
            for (int i = 0; ; i++) {
                if(i == 5){
                   // int j = i / 0;

                   // break;
                }
            }
        }
    }
}
