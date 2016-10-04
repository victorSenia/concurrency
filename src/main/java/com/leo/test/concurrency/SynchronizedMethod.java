package com.leo.test.concurrency;

/**
 * Created by Senchenko Victor on 03.10.2016.
 */
public class SynchronizedMethod {
    public static void main(String... args) throws InterruptedException {
        System.out.println("started main of class " + SynchronizedMethod.class.getSimpleName());
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
                System.out.println("Class " + GetData.class.getSimpleName() + " get data from store \"" + store.getData() + "\"");
                repeat--;
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
                store.setData(data + i);
                System.out.println("Class " + SetData.class.getSimpleName() + " set data to store \"" + data + i + "\"");
            }
        }
    }

    public static class Store {
        private StringBuilder builder = new StringBuilder();

        private boolean isEmpty() {
            return builder.length() == 0;
        }

        public synchronized String getData() {
            try {
                while (isEmpty())
                    try {
                        System.out.println("wait for data");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                System.out.println("data is set");
                return builder.toString();
            } finally {
                builder.setLength(0);
                notify();
            }
        }

        public synchronized void setData(String data) {
            while (!isEmpty())
                try {
                    System.out.println("wait for empty data");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            System.out.println("data empty");
            builder.append(data);
            notify();
        }
    }
}
