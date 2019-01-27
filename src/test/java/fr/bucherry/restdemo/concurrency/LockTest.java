package fr.bucherry.restdemo.concurrency;

import org.junit.Before;
import org.junit.Test;

import javax.interceptor.InvocationContext;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LockTest {
    private LockClient client1;
    private LockClient client2;
    private LockStore lockStore = MapBasedLockStore.getInstance();
    private Dummy jim;
    private Dummy joe;

    @Before
    public void setUp() throws Exception {
        client1 = new LockClient("client1");
        client2 = new LockClient("client2");

        jim = new Dummy("Jim");
        joe = new Dummy("Joe");

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


    @Test
    public void parallelCase1() throws Exception {
        InvocationContext call1 = new SimpleInvocationContext(jim, "sayHelloGoodbye", 123, "soleil");
        InvocationContext call2 = new SimpleInvocationContext(joe, "sayHelloGoodbye", 456, "soleil");

        Stream.of(call1, call2).parallel().forEach(call -> tryInvoke(client1, call));

        assertTrue(jim.end.isAfter(joe.start) && joe.end.isAfter(jim.start));
    }

    @Test
    public void contentionCase1() throws Exception {
        InvocationContext call1 = new SimpleInvocationContext(jim, "sayHelloGoodbye", 123, "soleil");
        InvocationContext call2 = new SimpleInvocationContext(joe, "sayHelloGoodbye", 123, "sirius");

        Stream.of(call1, call2).parallel().forEach(call -> tryInvoke(client1, call));

        assertFalse(jim.end.isAfter(joe.start) && joe.end.isAfter(jim.start));
    }

    private Object tryInvoke(LockClient lockClient, InvocationContext ctx) {
        try {
            return lockClient.aroundInvoke(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static class Dummy {
        private final String name;
        private Instant start;
        private Instant end;

        public Dummy(String name) {
            this.name = name;
        }

        // Annotated parameter is the one that matters for contention proceedings
        public Object sayHelloGoodbye(@Deprecated Integer myParam1, String myParam2) throws InterruptedException {
            start = Instant.now();
            System.out.println(name + ": Hello performing " + myParam1 + " " + myParam2 + " it's " + start);
            Thread.sleep(250);
            end = Instant.now();
            System.out.println(name + ": Bye it's " + end);
            return Duration.between(start, end);
        }
    }
}
