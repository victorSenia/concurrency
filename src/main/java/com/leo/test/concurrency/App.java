package com.leo.test.concurrency;

import com.leo.test.concurrency.concurrent.BlockingQueues;
import com.leo.test.concurrency.concurrent.Semaphores;

/**
 * Created by Senchenko Victor on 03.10.2016.
 */
public class App {
    public static void main(String... args) throws InterruptedException {
        WaitNotify.main();
        WaitUntilEmpty.main();
        SynchronizedMethod.main();
        BlockingQueues.main();
        Semaphores.main();
//                SimpleThreads.main("5");
    }
}
