package fr.bucherry.restdemo.concurrency;

import java.util.concurrent.locks.Lock;

public interface LockStore {
    Lock get(Object object);
}
