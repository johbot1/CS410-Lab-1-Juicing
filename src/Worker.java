import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Worker Class
 *
 * //[JB]
 */
public class Worker implements Runnable {
    //[JB]
    private final ConcurrentLinkedQueue<Orange> readyForWork;
    private final ConcurrentLinkedQueue<Orange> processedOranges;
    //[JB]
    private boolean isWorking;
    //[JB]
    private final Thread workerThread;
    //[JB]
    private volatile int orangeCounter;


    /**
     * Worker
     *
     * //[JB]
     * @param name Name of the Worker (i.e his assignment)
     * @param sendingTo   Oranges that have been processed, ready to go out
     * @param pullingFrom Oranges that are ready to be processed and bottled
     */
    Worker(String name, ConcurrentLinkedQueue<Orange> sendingTo, ConcurrentLinkedQueue<Orange> pullingFrom) {
        this.readyForWork = pullingFrom;
        this.processedOranges = sendingTo;
        this.workerThread = new Thread(this, "Worker Name: " + name);
        System.out.println("Worker: " + name + " created.");
        startWorking();
    }

    /**
     *
     */
    public void startWorking() {
        isWorking = true;
        orangeCounter = 0;
        workerThread.start();

    }

    /**
     * stopWorking
     *
     * //[JB]
     */
    public void stopWorking() {
        isWorking = false;
        clockOut();
    }

    /**
     * clockOut
     *
     * //[JB]
     */
    public void clockOut() {
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            System.err.println(workerThread.getName() + " was interrupted.");
        }
    }


    /**
     * getOrangeCounter
     * @return The Worker's Orange counter
     */
    public int getOrangeCounter() {
        return orangeCounter;
    }

    /**
     * run
     *
     * //[JB]
     */
    @Override
    public void run() {
        while (isWorking || (readyForWork != null && !readyForWork.isEmpty())) {
            if (readyForWork != null && !readyForWork.isEmpty()) {
                Orange o = Plant.getWork(readyForWork);
                if (o != null) {
                    o.runProcess();
                    Plant.sendWork(o, processedOranges);
                }
            } else if (readyForWork == null) { // If fetcher
                Orange o = new Orange();
                orangeCounter++;
                o.runProcess();
                Plant.sendWork(o, processedOranges);
            }

            try {
                Thread.sleep(10); // Prevent CPU overload when waiting for oranges
            } catch (InterruptedException e) {
                System.err.println(workerThread.getName() + " interrupted.");
            }
        }
    }
}