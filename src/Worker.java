import java.util.ArrayList;
import java.util.Queue;

/**
 * Worker Class
 *  The Worker is responsible for taking an orange from the shared queue and processing it completely;
 *  From its initial state (Fetched) all the way through to Processed.
 */
public class Worker implements Runnable{
    private final Plant plant;
    private final OrangeQueue orangeQueue;

    public Worker (Plant plant, OrangeQueue orangeQueue){
        this.plant = plant;
        this.orangeQueue = orangeQueue;
    }

    @Override
    public void run() {
        while (true) {
            // Retrieve an orange from the shared queue.
            // The dequeue() method will block if the queue is empty.
            Orange orange = orangeQueue.dequeue();
            // If a (null) is received, shutdown the worker.
            if (orange == null) {
                System.out.println(Thread.currentThread().getName() + " shutting down.");
                break;
            }
            // Process the orange completely from Fetched to Processed.
            while (orange.getState() != Orange.State.Processed) {
                orange.runProcess();
            }

            // Update the processed counter in the plant
//            System.out.println(Thread.currentThread().getName() + " finished processing an orange. Updating count.");
            plant.incrementProcessedOranges();
            System.out.println("Total oranges processed: " + plant.getProcessedOranges());

        }
    }
}
