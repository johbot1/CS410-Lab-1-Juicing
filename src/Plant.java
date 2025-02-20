import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <h1>Plant</h1>
 * Central control for the orange juice bottling plant simulation.
 * <p>
 * Manages the entire juice production process, from orange intake to bottling, using multithreading
 * for data and task parallelism.
 * </p>
 *
 * <h2>Parallelism:</h2>
 * <ul>
 *     <li><b>Data Parallelism:</b> Multiple {@code Plant} instances process separate orange sets.</li>
 *     <li><b>Task Parallelism:</b> Within each {@code Plant}, {@link Worker} threads perform parallel processing stages.</li>
 * </ul>
 *
 * <p>
 * Manages {@link Worker} threads and the flow of {@link Orange} objects between them using concurrent queues.
 * Sets up the factory environment for parallel orange processing.
 * </p>
 *
 * @Author Nate Williams
 * @Author John Botonakis
 */
public class Plant implements Runnable {
    /**
     * Processing time for each plant in milliseconds.
     */
    public static final long PROCESSING_TIME = 5 * 1000;
    /**
     * Oranges needed per bottle.
     */
    private final static int ORANGES_PER_BOTTLE = 4;
    /**
     * Number of plant instances.
     */
    private static final int NUM_PLANTS = 2;
    /**
     * Number of worker threads per plant.
     */
    private final static int NUM_WORKERS = 5;
    /**
     * Flag to ensure shutdown messages are printed only once.
     */
    private static boolean shutdownPrinted = false;
    /**
     * Plant's thread.
     */
    private final Thread thread;
    /**
     * Array of worker threads.
     */
    private final Worker[] workers = new Worker[NUM_WORKERS];
    /**
     * List of queues (not directly used, queues are managed as individual ConcurrentLinkedQueues).
     */ // Corrected Javadoc
    private final List<Orange> queues = new ArrayList<Orange>(NUM_WORKERS); // Corrected Javadoc
    /**
     * Count of oranges fetched by this plant.
     */
    public int orangesProvided;
    /**
     * Instantiates a Concurrent Linked Queue which is a FIFO node based queue that ""should"" be thread safe
     */
    public ConcurrentLinkedQueue<Orange> peelingQueue = new ConcurrentLinkedQueue<Orange>();
    public ConcurrentLinkedQueue<Orange> squeezingQueue = new ConcurrentLinkedQueue<Orange>();
    public ConcurrentLinkedQueue<Orange> bottlingQueue = new ConcurrentLinkedQueue<Orange>();
    public ConcurrentLinkedQueue<Orange> processedOranges = new ConcurrentLinkedQueue<Orange>();
    /**
     * Count of oranges fully processed by this plant.
     */
    private int orangesProcessed;
    /**
     * Volatile boolean indicating if the plant is working/running.
     * Volatile ensures that it is updated and seen across all threads
     */
    private volatile boolean timeToWork;
    /**
     * Instantiates 5 new workers. Hold onto them until actual worker creation
     */
    private Worker fetcher;
    private Worker peeler;
    private Worker squeezer;
    private Worker bottler;


    /**
     * Constructs a {@code Plant} instance. Initializes orange counts and creates the plant's thread.
     *
     * @param threadNum Plant instance identifier.
     */
    Plant(int threadNum) {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, Plant.class.getSimpleName() + " " + threadNum);
    }

    /**
     * Creates and starts multiple {@code Plant} instances, lets them process for a set time,
     * then stops and summarizes the production results.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(i);
            plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        int actualProvided = totalProvided - totalWasted;  // Adjust for waste
        System.out.println("Total provided/processed = " + actualProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles + ", wasted " + totalWasted + " oranges");

        //[JB] I'm not sure how, but there is an issue in how the original summary is printed
        //For some reason, the totalProvided doesn't have the amount subtracted properly
//        System.out.println("Nate's Way");
//        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
//        System.out.println("Created " + totalBottles +
//                ", wasted " + totalWasted + " oranges");
    }

    /**
     * Delays the current thread for a specified time.
     *
     * @param time   Delay duration in milliseconds.
     * @param errMsg Error message to print if interrupted.
     */
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    /**
     * Retrieves an {@link Orange} from the input queue if available. Thread-safe.
     *
     * @param inputList Queue to retrieve from.
     * @return {@link Orange} if dequeued, {@code null} if queue is empty.
     */
    public synchronized static Orange getOranges(ConcurrentLinkedQueue<Orange> inputList) {
        if (!inputList.isEmpty()) {
            Orange o = inputList.poll();
            return o;
        } else {
            return null;
        }
    }

    /**
     * Adds an {@link Orange} to the export queue. Thread-safe.
     *
     * @param orange     {@link Orange} to send.
     * @param exportList Queue to add to.
     */
    public synchronized static void sendOranges(Orange orange, ConcurrentLinkedQueue<Orange> exportList) {
        exportList.add(orange);
    }

    /**
     * Starts the plant's thread, initiating orange processing.
     */
    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    /**
     * Signals the plant to stop processing.
     */
    public void stopPlant() {
        timeToWork = false;
    }

    /**
     * Waits for plant thread to complete by calling join on any other threads
     *
     * @throws InterruptedException if plant thread join is interrupted.
     */
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * * Plant's main run loop. Creates workers, then waits for {@link #timeToWork} to be set to false,
     * after which it initiates worker shutdown.
     */
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Processing oranges");
        createWorkers();
        while (timeToWork) {
        }
        //Stop the workers after this call
        quittinTime();
        //Print out a message that says done
        System.out.println(" ");
    }

    /**
     * Creates and initializes all worker threads for this plant instance.
     * Workers are created for fetching, peeling, squeezing, and bottling.
     */
    private void createWorkers() {
        String[] workerNames = {"fetcher", "peeler", "squeezer", "bottler"};
        fetcher = new Worker("fetcher", peelingQueue, null);

        peeler = new Worker("peeler", squeezingQueue, peelingQueue);

        squeezer = new Worker("squeezer", bottlingQueue, squeezingQueue);

        bottler = new Worker("bottler", processedOranges, bottlingQueue);
    }

    /**
     * Gets the count of oranges fetched by this plant.
     *
     * @return Number of oranges provided.
     */
    public int getProvidedOranges() {
        orangesProvided = fetcher.getOrangeCounter();
        return orangesProvided;
    }

    /**
     * Gets the count of oranges fully processed by this plant.
     *
     * @return Number of oranges processed.
     */
    public int getProcessedOranges() {
        orangesProcessed = processedOranges.size();
        return orangesProcessed;
    }

    /**
     * Calculates the number of bottles produced.
     *
     * @return Number of bottles, based on {@link #ORANGES_PER_BOTTLE}.
     */
    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    /**
     * Calculates the number of oranges wasted (remainder and unprocessed).
     *
     * @return Number of wasted oranges.
     */
    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE + (orangesProvided - orangesProcessed);
    }

    /**
     * Initiates worker shutdown sequence. Stops workers, waits briefly, and prints final queue sizes.
     * Uses a synchronized block to ensure shutdown messages are printed only once across plants.
     */
    private void quittinTime() {
        System.out.println("Signaling workers to stop...");

        // Step 1: Stop workers
        fetcher.stopWorking();
        peeler.stopWorking();
        squeezer.stopWorking();
        bottler.stopWorking();

        // Step 2: Give workers some time to finish up remaining work
        try {
            Thread.sleep(500); // Give workers half a second to wrap up their queues
        } catch (InterruptedException e) {
            System.err.println("Error while pausing for workers to finish.");
        }

        // Step 3: Final queue cleanup ensuring it's only printed once
        // Only print queue sizes if it hasn’t been printed before
        synchronized (Plant.class) {
            if (!shutdownPrinted) {
                shutdownPrinted = true;
                //Mainly for debugging however a great visualization of the queues at the end
                //Because of this, I choose to keep it in.
                System.out.println("Final queue sizes before shutdown:");
                System.out.println("Peeling queue: " + peelingQueue.size());
                System.out.println("Squeezing queue: " + squeezingQueue.size());
                System.out.println("Bottling queue: " + bottlingQueue.size());

                // Log remaining peeled oranges
                if (!peelingQueue.isEmpty()) {
                    System.err.println("WARNING: Some oranges were left not peeled.");
                }
                //Log remaining squeezed oranges
                if (!squeezingQueue.isEmpty()) {
                    System.err.println("WARNING: Some oranges were left not squoze.");
                }

                // Log if remaining oranges to be bottled
                if (!bottlingQueue.isEmpty()) {
                    System.err.println("WARNING: Some oranges were left not bottled.");
                }

                System.out.println("All workers have been stopped.");
            }
        }
    }
}

