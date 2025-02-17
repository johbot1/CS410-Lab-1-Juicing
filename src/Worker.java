import java.util.Queue;

class Worker implements Runnable {
    private static final Object lock = new Object();
    private final Plant plant;
    private final int workerId;
    private final Queue<Orange> orangeQueue;
    private Thread workerThread;

    Worker(Plant plant, int workerId) {
        this.plant = plant;
        this.workerId = workerId;
        this.orangeQueue = plant.orangeQueue;
        this.workerThread = new Thread(this);
        System.out.println("Worker has been created.");
    }


    @Override
    public void run() {
        System.out.println("Worker 1 on begin work");
    }

    public synchronized void beginWork(Orange o) {
        synchronized (lock) {
            if (!plant.orangeQueue.isEmpty()) {
//                System.out.println("Able to Grab an orange.");
                o.runProcess();
//                System.out.println("Worker 1 on " + plant + " is fetching the orange.");
                // Fetch
                while (o.getState() == Orange.State.Fetched)
                    o.runProcess();

//                System.out.println("Fetching complete. Worker 1 is now peeling the orange.");
                // Peel
                while (o.getState() == Orange.State.Peeled) {
                    o.runProcess();  // Move the orange to the next state (Squeezed)
                }

//                System.out.println("Peeling completed. Worker 1 is now squeezing the orange.");
                // Squeeze
                while (o.getState() == Orange.State.Squeezed) {
                    o.runProcess();  // Move the orange to the next state (Processed)
//                    System.out.println("Squeezing complete. Worker 1 is now fetching more oranges.");
                    plant.orangeQueue.remove();
                }

                // Notify Worker 2 that Worker 1 is done
                lock.notify();  // Notify Worker 2 that Worker 1 is done
            } else {
                System.out.println("Worker on " + plant + " has ran out of oranges.");
                workerThread.interrupt();
            }
        }
    }

    private void processWithWorker2(Orange o) {
        synchronized (lock) {
            try {
                // Wait until Worker 1 finishes its tasks
                lock.wait();
                System.out.println("Worker 2 waitin.");
            } catch (InterruptedException e) {
                System.err.println("Worker 2 interrupted.");
            }

            // After Worker 1 finishes, Worker 2 processes the orange
            System.out.println("Worker 2 on " + plant + " is bottling the orange.");
            while (o.getState() == Orange.State.Processed) {
                o.runProcess();  // Move the orange to the final state (Bottled)
            }
        }
    }

}
