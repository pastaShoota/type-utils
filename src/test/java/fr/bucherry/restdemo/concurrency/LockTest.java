package fr.bucherry.restdemo.concurrency;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LockTest {
    private LockClient client1;
    private LockClient client2;
    private LockStore lockStore = MapBasedLockStore.getInstance();

    @Before
    public void setUp() throws Exception {
        client1 = new LockClient("client1");
        client2 = new LockClient("client2");

        client1.setLockStore(lockStore);
        client2.setLockStore(lockStore);
    }

    @Test
    public void contentionCase() {
        Stream.of(client1, client2).parallel().forEach(client -> client.sayHello("you"));

        assertFalse(client1.end.isAfter(client2.start) && client2.end.isAfter(client1.start));
    }

    @Test
    public void parallelCase() {
        Stream.of(client1, client2).parallel().forEach(client -> client.sayHello(client.name));

        assertTrue(client1.end.isAfter(client2.start) && client2.end.isAfter(client1.start));
    }
}
