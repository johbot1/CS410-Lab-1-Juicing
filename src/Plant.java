/**
 * @Author Nate Williams
 * @Author John Botonakis
 *
 * Most of this code remains unmodified from the original download, however, anything added will be labeled as NEW
 */
public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;
    public static final int ORANGES_PER_BOTTLE = 4;
    private static final int NUM_PLANTS = 2;
    private final Thread thread;
    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork; //Volatile - Do NOT cache the data inside the thread

    // NEW: Shared queue for oranges produced by this plant
    private final OrangeQueue orangeQueue = new OrangeQueue();

    //NEW: Used this to print out clearer names as opposed to code gibberish
    @Override
    public String toString() {
        return "Plant " + thread.getName().replaceAll("[^0-9]", "");
    }


    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(i + 1);
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
        System.out.println("Created " + totalBottles + ", wasted " + totalWasted + " oranges");
        System.out.println("Oranges per bottle: " + ORANGES_PER_BOTTLE);
    }

    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }


    Plant(int threadNum) {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant[" + threadNum + "]");
    }

    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    public void stopPlant() {
        timeToWork = false;
    }

    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Processing oranges");
        while (timeToWork) {
            // NEW: Instead of processing the orange completely,
            // create a new Orange and enqueue it for the workers.
            Orange orange = new Orange();
            orangeQueue.enqueue(orange);
            orangesProvided++;
//            System.out.println(Thread.currentThread().getName() + " processed an orange. Total processed: " + orangesProcessed);


//            Just in case I cannot actually modify Plant code
//            processEntireOrange(new Orange());
//            orangesProvided++;
//            System.out.print(".");
        }
        System.out.println(Thread.currentThread().getName() + " has stopped processing.");
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    public void processEntireOrange(Orange o) {
        while (o.getState() != Orange.State.Bottled) {
            o.runProcess();
        }
        orangesProcessed++;
    }

    // NEW: Called by worker threads when they finish processing an orange.
    public synchronized void incrementProcessedOranges() {
        orangesProcessed++;
    }

    public int getProvidedOranges() {
        return orangesProvided;
    }

    public int getProcessedOranges() {
        return orangesProcessed;
    }

    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }

    // NEW: Getter to expose the orange queue for worker threads.
    public OrangeQueue getOrangeQueue() {
        return orangeQueue;
    }
}
