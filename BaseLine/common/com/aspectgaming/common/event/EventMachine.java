package com.aspectgaming.common.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventMachine {

    private final Queue<EventBody> queue = new ConcurrentLinkedQueue<>();

    private static final EventMachine instance = new EventMachine();

    public static EventMachine getInstance() {
        return instance;
    }

    private EventMachine() {}

    public void offerEvent(Class<? extends GameEvent> event) {
        EventBody msg = new EventBody();
        msg.event = event;
        queue.offer(msg);
    }

    public void offerEvent(Class<? extends GameEvent> event, Object... args) {
        EventBody msg = new EventBody();
        msg.event = event;
        msg.args = args;
        queue.offer(msg);
    }

    public EventBody takeEvent() {
        return queue.poll();
    }
}
