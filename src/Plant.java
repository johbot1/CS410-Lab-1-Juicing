/**
 * @Author Nate Williams
 * @Author John Botonakis
 *
 * This Plant class represents a factory-esque operation where each instance simulates a plant
 * that processes oranges into bottled juice. The class uses "Runnable" which means that this can
 * be executed on its own thread.
 *
 * Most code here is unchanged, with any comments done by me being stated
 */
public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;
    //[JB] The amount of oranges required to produce one bottle
    private final static int ORANGES_PER_BOTTLE = 4;
    //[JB] The number of plants to be created
    private static final int NUM_PLANTS = 2;
    private final Thread thread;
    //[JB] Tracks the amount of oranges provided/processed by the plant
    private int orangesProvided;
    private int orangesProcessed;
    //[JB] Volatile boolean indicating if the plant is working/running
    //Volatile ensures that it is updated and seen across all threads
    private volatile boolean timeToWork;


    /**
     * [JB]
     * Creates an array of plants of "NUM_PLANTS" size, and initalizes them
     * After, it waits for the plants to finish processing, then, it stops each
     * one and sets the timeToWork flag to false. waitToStop is called after for each
     * plant's thread to finish by calling join()
     * @param args
     */
    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(1);
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
     * [JB]
     * Utility to pause the program giving some tiny degree of randomness
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
     * [JB]
     * Constructor initilaizes the oranges provided/processed to 0
     * It also creates a new thread, giving it the run method, and a unique name
     *
     * @param threadNum a uniquely named new thread object
     */
    Plant(int threadNum) {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant[" + threadNum + "]");
    }


    //[JB] Sets timeToWork to true, and starts the thread
    public void startPlant() {
        timeToWork = true;
        thread.start();
    }
    //[JB] Sets timeToWork to false, signalling it should stop processing
    public void stopPlant() {
        timeToWork = false;
    }

    //[JB] Waits for plant thread to complete by calling join on any other threads
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * [JB]
     * Processes oranges while timeToWork is true.
     * Prints out info about which plant is processing oranges; For each
     * orange, the entire process is called and orangesProvided incremented.
     */
    public void run() {
        System.out.print(Thread.currentThread().getName() + " Processing oranges");
        while (timeToWork) {
            processEntireOrange(new Orange());
            orangesProvided++;
            System.out.print(".");
        }
        System.out.println("");
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    /**
     * [JB]
     * Calls runProcess until the orange is bottled
     * After, orangesProcessed is incremented
     * @param o
     */
    public void processEntireOrange(Orange o) {
        while (o.getState() != Orange.State.Bottled) {
            o.runProcess();
        }
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
}
