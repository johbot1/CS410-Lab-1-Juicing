import java.util.LinkedList;
import java.util.Queue;

public class OrangeQueue {
    private final Queue<Orange> queue = new LinkedList<>();

    public synchronized void enqueue(Orange o) {
        queue.add(o);
        notifyAll(); // Wake up any threads waiting in dequeue()
    }
    public synchronized Orange dequeue() {
        while (queue.isEmpty()) {
            try {
                wait(); // Wait until an orange is enqueued
            } catch (InterruptedException e) {
                System.err.println("OrangeQueue: Interrupted while waiting for oranges.");
            }
        }
        return queue.poll();
    }
}
