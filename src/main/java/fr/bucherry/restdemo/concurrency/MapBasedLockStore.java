package fr.bucherry.restdemo.concurrency;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This implementation is only intended for test environment.
 */
public class MapBasedLockStore implements LockStore {

    private static MapBasedLockStore instance;

    private static final int MAX_SIZE = 100;

    private final Map<Object, Lock> store = new ConcurrentHashMap<>();

    private MapBasedLockStore() {
    }

    @Override
    public synchronized Lock get(Object key) {
        if (!store.containsKey(key)) {
            cleanStoreIfMaxSizeReached();
            store.put(key, new ReentrantLock());
        }
        return store.get(key);
    }

    private void cleanStoreIfMaxSizeReached() {
        if (store.size() > MAX_SIZE) {
            Iterator keys = store.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                Lock lock = store.get(key);
                if (lock == null || lock.tryLock()) {
                    keys.remove();
                }
            }
        }
    }

    public static MapBasedLockStore getInstance() {
        if (instance == null) {
            instance = new MapBasedLockStore();
        }
        return instance;
    }
}
