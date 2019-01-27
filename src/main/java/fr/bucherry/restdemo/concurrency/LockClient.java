package fr.bucherry.restdemo.concurrency;

import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

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

    public Object aroundInvoke(InvocationContext ctx) throws Exception {
        String lockName = getLockName(ctx);
        Lock lock = lockStore.get(lockName);
        lock.lock();

        try {
            return ctx.proceed();
        } finally {
            lock.unlock();
        }
    }

    private static String getLockName(InvocationContext ctx) {
        StringBuilder lockName = new StringBuilder();
        lockName.append(ctx.getMethod().getName());

        Object[] parameters = ctx.getParameters();
        Annotation[][] parametersAnnotations = ctx.getMethod().getParameterAnnotations();
        int length = Math.min(parameters.length, parametersAnnotations.length);

        for (int i=0; i<length; i++) {
            Annotation[] parameterAnnotations = parametersAnnotations[i];
            if (Stream.of(parameterAnnotations).anyMatch(ann -> ann instanceof Deprecated)) {
                lockName.append(parameters[i]);
            }
        }
        return lockName.toString();
    }
}
