package com.leo.test.concurrency;

/**
 * Created by Senchenko Victor on 03.10.2016.
 */
public class WaitUntilEmpty {
    public static void main(String... args) throws InterruptedException {
        System.out.println("started main of class " + WaitUntilEmpty.class.getSimpleName());
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
                synchronized (store) {
                    while (store.isEmpty())
                        waitData();
                    System.out.println("Class " + GetData.class.getSimpleName() + " get data from store \"" + store.getData() + "\"");
                    store.notify();
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
                synchronized (store) {
                    while (!store.isEmpty())
                        waitEmpty();
                    store.setData(data + i);
                    System.out.println("Class " + SetData.class.getSimpleName() + " set data to store \"" + data + i + "\"");
                    store.notify();
                    System.out.println("Class " + SetData.class.getSimpleName() + " notify");
                }
            }
        }

        private void waitEmpty() {
            try {
                System.out.println("Class " + SetData.class.getSimpleName() + " wait until empty");
                store.wait();
                System.out.println("Class " + SetData.class.getSimpleName() + " after wait until empty");
            } catch (InterruptedException e) {
                e.printStackTrace();
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
