import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author Nate Williams
 * @Author John Botonakis
 * <p>
 * This Plant class represents a factory-esque operation where each instance simulates a plant
 * that processes oranges into bottled juice. The class uses "Runnable" which means that this can
 * be executed on its own thread.
 * <p>
 * Some functions here remain unchanged, with any comments done by me being stated
 */
public class Plant implements Runnable {
    //[JB] Put all vars up at the top for easier readability
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;
    //[JB] The amount of oranges required to produce one bottle
    private final static int ORANGES_PER_BOTTLE = 4;
    //[JB] The number of plants to be created
    private static final int NUM_PLANTS = 2;
    //[JB] Magic number for number of workers, one for each job
    private final static int NUM_WORKERS = 5;
    //[JB] Tracks the amount of oranges provided/processed by the plant
    public int orangesProvided;
    public ConcurrentLinkedQueue<Orange> peelingQueue = new ConcurrentLinkedQueue<Orange>();
    public ConcurrentLinkedQueue<Orange> squeezingQueue = new ConcurrentLinkedQueue<Orange>();
    public ConcurrentLinkedQueue<Orange> bottlingQueue = new ConcurrentLinkedQueue<Orange>();
    public ConcurrentLinkedQueue<Orange> processorQueue = new ConcurrentLinkedQueue<Orange>();
    //[JB] Creates a Thread to be used later
    private final Thread thread;
    private int orangesProcessed;
    //[JB] Volatile boolean indicating if the plant is working/running
    //Volatile ensures that it is updated and seen across all threads
    private volatile boolean timeToWork;
    //[JB] Creates a storage area for workers
    private final Worker[] workers = new Worker[NUM_WORKERS];
    //[JB] Creates a storage area for queues
    private final List<Orange> queues = new ArrayList<Orange>(NUM_WORKERS);
    private Worker fetcher;
    private Worker peeler;
    private Worker squeezer;
    private Worker bottler;
    /**
     * //[JB]
     * Constructor initializes the oranges provided/processed to 0
     * It also creates a new thread, giving it the run method, and a unique name
     *
     * @param threadNum a thread number to identify and separate plant objects
     */
    Plant(int threadNum) {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, Plant.class.getSimpleName() + " " + threadNum);
    }

    /**
     * //[JB]
     * Creates an array of plants of "NUM_PLANTS" (2) size and initializes them
     * After creation, each of the plants starts it's work.
     * The delay call gives the plants time to work.
     * Once the work day is done, the plants stop, then join the workers together,
     * summarizing the day's production.
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
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles +
                ", wasted " + totalWasted + " oranges");
    }

    /**
     * //[JB]
     * Delays the plant thread, allowing the workers to work
     *
     * @param time
     * @param errMsg
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
     * //[JB]
     * Sets timeToWork to true, and starts the thread
     */
    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    /**
     * //[JB]
     * Sets timeToWork to false, signalling it should stop processing
     */
    public void stopPlant() {
        timeToWork = false;
    }

    /**
     * //[JB]
     * Waits for plant thread to complete by calling join on any other threads
     */
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * //[JB]
     * While it is time to work, do nothing as it's a plant and doesn't need to work.
     * After it's done, begin to slow down production to a halt, eventually stopping altogether.
     * Finally, print a blank line for better spacing of the production summary
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
     * //[JB]
     * createWorkers
     * Creates each of the workers that will be working in the Factory
     */
    private void createWorkers() {
        String[] workerNames = {"fetcher", "peeler", "squeezer", "bottler"};
        fetcher = new Worker("fetcher", peelingQueue, null);

        peeler = new Worker("peeler", squeezingQueue, peelingQueue);

        squeezer = new Worker("squeezer", bottlingQueue, squeezingQueue);

        bottler = new Worker("bottler", processorQueue, bottlingQueue);
    }

    /**
     * //[JB]
     * getProvidedOranges
     *
     * @return
     */
    public int getProvidedOranges() {
        orangesProvided = fetcher.getOrangeCounter();
        return orangesProvided;
    }

    /**
     * //[JB]
     * getProcessedOranges
     *
     * @return
     */
    public int getProcessedOranges() {
        orangesProcessed = processorQueue.size();
        return orangesProcessed;
    }

    /**
     * //[JB]
     * getBottles
     *
     * @return
     */
    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    /**
     * //[JB]
     * getWaste
     *
     * @return
     */
    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE + (orangesProvided - orangesProcessed);
    }

    /**
     * //[JB]
     * quittinTime
     * This calls the ".join" method on each of the workers,
     * So long as they are not actively busy, shutting them down.
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
            Thread.sleep(500); // Give workers half a second to wrap up
        } catch (InterruptedException e) {
            System.err.println("Error while pausing for workers to finish.");
        }

        // Step 3: Final queue cleanup
        System.out.println("Final queue sizes before shutdown:");
        System.out.println("Peeling queue: " + peelingQueue.size());
        System.out.println("Squeezing queue: " + squeezingQueue.size());
        System.out.println("Bottling queue: " + bottlingQueue.size());
        System.out.println("Processor queue: " + processorQueue.size());

        // Step 4: Log any remaining oranges
        if (!processorQueue.isEmpty()) {
            System.err.println("WARNING: Some oranges were left unprocessed.");
        }
        if (!peelingQueue.isEmpty()){
            System.err.println("WARNING: Some oranges were left not peeled.");
        }

        if (!squeezingQueue.isEmpty() ||
                !bottlingQueue.isEmpty() ){
            System.err.println("WARNING: Some oranges were left not squoze or bottled.");
        }

        System.out.println("All workers have been stopped.");
    }


    /**
     * //[JB]
     * createQueues
     * Creates 5 Distinct queues
     */
    private void createQueues() {
        String[] listNames = {"peelingQueue", "squeezingQueue", "bottlingQueue", "processorQueue"};
        for (int i = 0; i < NUM_WORKERS; i++) {
//            List<Orange>
        }
    }
}

