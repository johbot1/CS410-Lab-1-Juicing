import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {
    private final ConcurrentLinkedQueue<Orange> readyForWork;
    private final ConcurrentLinkedQueue<Orange> processedOranges;
    private boolean isWorking;
    private final Thread workerThread;
    private volatile int orangeCounter;


    /**
     * @param name Name of the Worker (i.e his assignment)
     * @param to   Oranges ready to be processed
     * @param from Oranges that have been processed, ready to go out
     */
    Worker(String name, ConcurrentLinkedQueue<Orange> to, ConcurrentLinkedQueue<Orange> from) {
        this.readyForWork = from;
        this.processedOranges = to;
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
     *
     */
    public void stopWorking() {
        isWorking = false;
        clockOut();
    }

    public void clockOut() {
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            System.err.println(workerThread.getName() + " was interrupted.");
        }
    }


    /**
     * @return The Worker's Orange counter
     */
    public int getOrangeCounter() {
        return orangeCounter;
    }

    /**
     *
     */
    @Override
    public void run() {
        while (isWorking) {
            if (readyForWork != null && !readyForWork.isEmpty()) {
                Orange o = JuiceBottler.getWork(readyForWork);
                if (o != null) {
                    o.runProcess();
                    JuiceBottler.sendWork(o, processedOranges);
                }
            } else if (readyForWork == null) { // If fetcher
                Orange o = new Orange();
                orangeCounter++;
                o.runProcess();
                JuiceBottler.sendWork(o, processedOranges);
            }

            try {
                Thread.sleep(10); // Prevent CPU overload when waiting for oranges
            } catch (InterruptedException e) {
                System.err.println(workerThread.getName() + " interrupted.");
            }
        }
    }
}