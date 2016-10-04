package com.leo.test.concurrency.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Senchenko Victor on 03.10.2016.
 */
public class BlockingQueues {
    public static void main(String... args) throws InterruptedException {
        System.out.println("started main of class " + BlockingQueues.class.getSimpleName());
        int repeat = 20;
        Store store = new Store();
        System.out.println("start class " + GetData.class.getSimpleName());
        GetData getData = new GetData(repeat, store);
        getData.start();
        SetData setData = new SetData(repeat, store);
        System.out.println("start class " + SetData.class.getSimpleName());
        setData.start();
        getData.join();
        setData.join();
    }

    public static class GetData extends Thread {
        private int repeat;

        private Store store;

        public GetData(int repeat, Store store) {
            this.repeat = repeat;
            this.store = store;
        }

        @Override
        public void run() {
            while (repeat > 0) {
                try {
                    repeat--;
                    System.out.println("Class " + GetData.class.getSimpleName() + " get data from store \"" + store.getData() + "\"");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class SetData extends Thread {
        private int repeat;

        private Store store;

        private final String data = "data";

        public SetData(int repeat, Store store) {
            this.repeat = repeat;
            this.store = store;
        }

        @Override
        public void run() {
            for (int i = 0; i < repeat; i++) {
                try {
                    store.setData(data + i);
                    System.out.println("Class " + SetData.class.getSimpleName() + " set data to store \"" + data + i + "\"");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Store {
        private BlockingQueue<String> queue = new LinkedBlockingQueue<>(1);

        public String getData() throws InterruptedException {
            return queue.take();
        }

        public void setData(String data) throws InterruptedException {
            queue.put(data);
        }
    }
}

