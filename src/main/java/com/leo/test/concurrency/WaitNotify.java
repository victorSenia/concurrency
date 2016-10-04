package com.leo.test.concurrency;

/**
 * Created by Senchenko Victor on 03.10.2016.
 * <p/>
 * Without sleep in storing data (ever for 0 milliseconds) occurs races and quantity of get and set different (App doesn't exit).
 * Thread.yield() doesn't work (at least on my PC).
 */
public class WaitNotify {
    public static void main(String... args) throws InterruptedException {
        System.out.println("started main of class " + WaitNotify.class.getSimpleName());
        int repeat = 2;
        Store store = new Store();
        System.out.println("start class " + GetData.class.getSimpleName());
        Thread getData = new Thread(new GetData(repeat, store));
        getData.start();
        Thread setData = new Thread(new SetData(repeat, store));
        System.out.println("start class " + SetData.class.getSimpleName());
        setData.start();
        getData.join();
        setData.join();
    }

    public static class GetData implements Runnable {
        private int repeat;

        private Store store;

        public GetData(int repeat, Store store) {
            this.repeat = repeat;
            this.store = store;
        }

        @Override
        public void run() {
            while (repeat > 0) {
                synchronized (store) {
                    while (store.isEmpty())
                        waitData();
                    System.out.println("Class " + GetData.class.getSimpleName() + " get data from store \"" + store.getData() + "\"");
                }
                repeat--;
            }
        }

        private void waitData() {
            try {
                System.out.println("Class " + GetData.class.getSimpleName() + " wait data");
                store.wait();
                System.out.println("Class " + GetData.class.getSimpleName() + " after wait data");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SetData implements Runnable {

        private int repeat;

        private Store store;

        private final int sleep = 00;

        private final String data = "data";

        public SetData(int repeat, Store store) {
            this.repeat = repeat;
            this.store = store;
        }

        @Override
        public void run() {
            for (int i = 0; i < repeat; i++) {
                //TODO
                try {
                    System.out.println("Class " + SetData.class.getSimpleName() + " sleep " + i + " time for " + sleep);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (store) {
                    store.setData(data + i);
                    System.out.println("Class " + SetData.class.getSimpleName() + " set data to store \"" + data + i + "\"");
                    store.notify();
                    System.out.println("Class " + SetData.class.getSimpleName() + " notify");
                }
            }
        }
    }

    public static class Store {
        private StringBuilder builder = new StringBuilder();

        public boolean isEmpty() {
            return builder.length() == 0;
        }

        public String getData() {
            try {
                return builder.toString();
            } finally {
                builder.setLength(0);
            }
        }

        public void setData(String data) {
            builder.append(data);
        }
    }
}
