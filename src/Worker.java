import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <h1>Worker</h1>
 * Represents a thread performing a processing stage in the juice plant.
 * <p>
 * Task parallelism is achieved with multiple {@code Worker} threads in a {@link Plant}, each processing
 * {@link Orange} objects through a specific stage.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li>Processes {@link Orange} objects from {@code readyForWork} queue.</li>
 *     <li>Executes processing stage via {@link Orange#runProcess()}.</li>
 *     <li>Passes processed {@link Orange} to {@code processedOranges} queue.</li>
 *     <li>Tracks processed orange count ({@link #orangeCounter}).</li>
 *     <li>Manages lifecycle: {@link #startWorking()}, {@link #stopWorking()}, {@link #clockOut()}.</li>
 * </ul>
 *
 * <p>
 * Managed by {@link Plant}, using concurrent queues for orange flow.
 * </p>
 */
public class Worker implements Runnable {
    // Initializes two queues that are Concurrent Linked Queues
    // which are a FIFO node based queue that ""should"" be thread safe
    private final ConcurrentLinkedQueue<Orange> readyForWork;
    private final ConcurrentLinkedQueue<Orange> processedOranges;
    //[JB] Each worker gets their own individual thread
    private final Thread workerThread;
    //[JB] Boolean indicating if the Worker is working
    private boolean isWorking;
    //[JB] Keeps track of the oranges that have been fully processed
    private volatile int orangeCounter;


    /**
     * Constructs a {@code Worker}. Sets up queues and starts working.
     *
     * @param name        Worker name (stage).
     * @param sendingTo   Queue to send processed {@link Orange} to.
     * @param pullingFrom Queue to get {@link Orange} to process from.
     */
    Worker(String name, ConcurrentLinkedQueue<Orange> sendingTo, ConcurrentLinkedQueue<Orange> pullingFrom) {
        this.readyForWork = pullingFrom;
        this.processedOranges = sendingTo;
        this.workerThread = new Thread(this, "Worker Name: " + name);
        System.out.println("Worker: " + name + " created.");
        startWorking();
    }

    /**
     * Starts the {@code Worker}'s thread and begins its processing loop.
     */
    public void startWorking() {
        isWorking = true;
        orangeCounter = 0;
        workerThread.start();

    }

    /**
     * Signals the {@code Worker} to stop processing and begin its shutdown sequence.
     */
    public void stopWorking() {
        isWorking = false;
        clockOut();
    }

    /**
     * Waits for the {@code Worker}'s thread to terminate gracefully.
     * <p>
     * This method calls {@link Thread#join()} on the worker's thread ({@link #workerThread}). This will block the
     * calling thread until the worker thread has completed its execution.  It is typically called after {@link #stopWorking()}
     * to ensure that the worker has finished processing and released any resources before the program exits or proceeds further.
     * </p>
     *
     * @throws InterruptedException if the thread waiting for the worker to join is interrupted. In this case, an error
     *                              message is printed to the standard error stream indicating that the worker thread was interrupted during shutdown.
     */
    public void clockOut() {
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            System.err.println(workerThread.getName() + " was interrupted.");
        }
    }


    /**
     * Gets the number of {@link Orange} objects processed by this {@code Worker}.
     *
     * @return The count of oranges that this worker has successfully processed.
     */
    public int getOrangeCounter() {
        return orangeCounter;
    }

    /**
     * The main processing loop for the {@code Worker} thread.
     * <p>
     * This method is executed when the worker's thread starts. It continuously attempts to retrieve {@link Orange} objects
     * from the {@code readyForWork} queue and process them. The loop continues as long as the worker is in the working state
     * ({@link #isWorking} is true) or there are still oranges remaining in the input queue.
     * </p>
     *
     * <p>
     * <b>Processing Logic:</b>
     * </p>
     * <ol>
     *     <li><b>Retrieve Orange:</b> Attempts to dequeue an {@link Orange} from the {@code readyForWork} queue using
     *     {@link Plant#getOranges(ConcurrentLinkedQueue)}.  If successful (an orange is retrieved), it proceeds to the next step.
     *     If the queue is empty or {@code readyForWork} is {@code null}, it may proceed to create a new {@link Orange} (see note below).</li>
     *     <li><b>Process Orange:</b> Calls {@link Orange#runProcess()} on the retrieved orange. This advances the orange to its
     *     next {@link Orange.State} in the production process and simulates the processing time.</li>
     *     <li><b>Send Processed Orange:</b> Enqueues the processed {@link Orange} to the {@code processedOranges} queue using
     *     {@link Plant#sendOranges(Orange, ConcurrentLinkedQueue)}, making it available for the next stage worker.</li>
     *     <li><b>Increment Counter:</b> Increments the {@link #orangeCounter} to track the number of oranges processed by this worker.</li>
     *     <li><b>Spin Wait/Hibernate:</b> Introduces a small delay {@code Thread.sleep(10)} to implement a spin wait or short hibernation.
     *     This reduces CPU usage when the worker is waiting for oranges to become available in the input queue.</li>
     * </ol>
     *
     *
     * <p>
     * <b>Error Handling:</b> If the thread sleep is interrupted, an error message is printed to the standard error stream, indicating
     * that the worker thread was interrupted.
     * </p>
     */
    @Override
    public void run() {
        while (isWorking || (readyForWork != null && !readyForWork.isEmpty())) {
            if (readyForWork != null && !readyForWork.isEmpty()) {
                Orange o = Plant.getOranges(readyForWork);
                if (o != null) {
                    o.runProcess();
                    Plant.sendOranges(o, processedOranges);
                }
            } else if (readyForWork == null) {
                Orange o = new Orange();
                orangeCounter++;
                o.runProcess();
                Plant.sendOranges(o, processedOranges);
            }

            try {
                Thread.sleep(10); // Spin wait / Hibernate
            } catch (InterruptedException e) {
                System.err.println(workerThread.getName() + " interrupted.");
            }
        }
    }
}