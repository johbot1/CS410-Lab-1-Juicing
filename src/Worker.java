import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {
    private boolean isWorking;
    private String workerName;

    private Thread workerThread;
    private volatile int orangeCounter;


    private ConcurrentLinkedQueue<Orange> fromList;
    private ConcurrentLinkedQueue<Orange> toList;


    Worker(String name, ConcurrentLinkedQueue<Orange> to, ConcurrentLinkedQueue<Orange> from) {
        this.fromList = from;
        this.toList = to;
        this.workerName = name;
        this.workerThread = new Thread(this, "Worker Name: " + name);
        System.out.println("Worker: " + name + " created.");
        startWorking();
    }


    public void startWorking() {
        isWorking = true;
//        System.out.println("Worker: " + this.workerName + " is now working!");
        orangeCounter = 0;
        workerThread.start();

    }

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

    public String getWorkerName() {
        return workerName;
    }

    public ConcurrentLinkedQueue<Orange> getToList() {
        return toList;
    }

    public ConcurrentLinkedQueue<Orange> getFromList() {
        return fromList;
    }

    public int getOrangeCounter() {
        return orangeCounter;
    }

    @Override
    public void run() {
        while (isWorking) {
            if (fromList != null && !fromList.isEmpty()) {
                Orange o = JuiceBottler.getWork(fromList);
                if (o != null) {
                    o.runProcess();
                    JuiceBottler.sendWork(o, toList);
                }
            } else if (fromList == null) { // If fetcher
                Orange o = new Orange();
                orangeCounter++;
                o.runProcess();
                JuiceBottler.sendWork(o, toList);
            }

            try {
                Thread.sleep(10); // Prevent CPU overload when waiting for oranges
            } catch (InterruptedException e) {
                System.err.println(workerThread.getName() + " interrupted.");
            }
        }
    }
}