package fr.bucherry.restdemo.concurrency;

import java.time.Instant;
import java.util.concurrent.locks.Lock;

public class LockClient {

    final String name;
    LockStore lockStore;
    Instant start;
    Instant end;

    public LockClient(String name) {
        this.name = name;
    }

    public void sayHello(Object arg) {

        Lock lock = lockStore.get(arg);
        lock.lock();

        try {
            start = Instant.now();
            System.out.println(start + " " + name + ": Hello starting processing of argument " + arg + " lock: "+ lock);
            Thread.sleep(500);
            end = Instant.now();
            System.out.println(end + " " + name + ": Bye finished processing of argument " + arg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void setLockStore(LockStore lockStore) {
        this.lockStore = lockStore;
    }
}
