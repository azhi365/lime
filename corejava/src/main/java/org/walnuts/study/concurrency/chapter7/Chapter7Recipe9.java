package org.walnuts.study.concurrency.chapter7;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 实现一个自定义的Lock类
 */
public class Chapter7Recipe9 {


    public static void main(String[] args) {

        MyLock lock = new MyLock();

        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task-" + i, lock);
            Thread thread = new Thread(task);
            thread.start();
        }

		/*
		 * The main thread also tries to get the lock
		 */
        boolean value;
        do {
            try {
                value = lock.tryLock(1, TimeUnit.SECONDS);
                if (!value) {
                    System.out.printf("Main: Trying to get the Lock\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                value = false;
            }
        } while (!value);
		

        System.out.printf("Main: Got the lock\n");
        lock.unlock();
		
        System.out.printf("Main: End of the program\n");
    }

    public static class MyAbstractQueuedSynchronizer extends AbstractQueuedSynchronizer {


        private static final long serialVersionUID = 1L;

        private AtomicInteger state;

        public MyAbstractQueuedSynchronizer() {
            state = new AtomicInteger(0);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return state.compareAndSet(0, 1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            return state.compareAndSet(1, 0);
        }
    }

    public static class MyLock implements Lock {


        private AbstractQueuedSynchronizer sync;


        public MyLock() {
            sync = new MyAbstractQueuedSynchronizer();
        }

        @Override
        public void lock() {
            sync.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            try {
                return sync.tryAcquireNanos(1, 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        public boolean tryLock(long time, TimeUnit unit)
                throws InterruptedException {
            return sync.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
        }


        @Override
        public void unlock() {
            sync.release(1);
        }


        @Override
        public Condition newCondition() {
            return sync.new ConditionObject();
        }

    }

    public static class Task implements Runnable {

        private MyLock lock;
        private String name;


        public Task(String name, MyLock lock) {
            this.lock = lock;
            this.name = name;
        }


        @Override
        public void run() {
            lock.lock();
            System.out.printf("Task: %s: Take the lock\n", name);
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.printf("Task: %s: Free the lock\n", name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }


}
