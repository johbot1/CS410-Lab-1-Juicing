class Worker implements Runnable {
    private final Plant plant;
    private final int workerId;
    private final Orange orange;

    private static final Object lock = new Object();

    Worker(Plant plant, int workerId, Orange orange) {
        this.plant = plant;
        this.workerId = workerId;
        this.orange = orange;
    }

    @Override
    public void run() {
        if (workerId == 1) {
            processWithWorker1();
        } else if (workerId == 2) {
            processWithWorker2();
        }
    }

    private void processWithWorker1() {
        synchronized (lock) {
            System.out.println("Worker 1 on " + plant + " is fetching the orange.");
            // Fetch
            while (orange.getState() == Orange.State.Fetched)
                orange.runProcess();

            System.out.println("Worker 1 on " + plant + " is peeling the orange.");
            // Peel
            while (orange.getState() == Orange.State.Peeled) {
                orange.runProcess();  // Move the orange to the next state (Squeezed)
            }

            System.out.println("Worker 1 on " + plant + " is squeezing the orange.");
            // Squeeze
            while (orange.getState() == Orange.State.Squeezed) {
                orange.runProcess();  // Move the orange to the next state (Processed)
            }

            // Notify Worker 2 that Worker 1 is done
            lock.notify();  // Notify Worker 2 that Worker 1 is done
        }
    }

    private void processWithWorker2() {
        synchronized (lock) {
            try {
                // Wait until Worker 1 finishes its tasks
                lock.wait();
            } catch (InterruptedException e) {
                System.err.println("Worker 2 interrupted.");
            }

            // After Worker 1 finishes, Worker 2 processes the orange
            System.out.println("Worker 2 on " + plant + " is bottling the orange.");
            while (orange.getState() == Orange.State.Processed) {
                orange.runProcess();  // Move the orange to the final state (Bottled)
            }
        }
    }

}
