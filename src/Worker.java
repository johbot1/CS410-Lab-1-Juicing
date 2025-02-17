class Worker implements Runnable {
    private static final Object lock = new Object();
    private final Plant plant;
    private final int workerId;
    private final Orange orange;

    Worker(Plant plant, int workerId, Orange orange) {
        this.plant = plant;
        this.workerId = workerId;
        this.orange = orange;
    }


    @Override
    public void run() {
        System.out.println("Worker 1 on begin work");
    }

    public synchronized void beginWork(Orange o) {
        processWithWorker1(o);
    }

    private void processWithWorker1(Orange o) {
        synchronized (lock) {
            if (o != null) {
                o.runProcess();
                System.out.println("Worker 1 on " + plant + " is fetching the orange.");
                // Fetch
                while (o.getState() == Orange.State.Fetched)
                    o.runProcess();

                System.out.println("Worker 1 on " + plant + " is peeling the orange.");
                // Peel
                while (o.getState() == Orange.State.Peeled) {
                    o.runProcess();  // Move the orange to the next state (Squeezed)
                }

                System.out.println("Worker 1 on " + plant + " is squeezing the orange.");
                // Squeeze
                while (o.getState() == Orange.State.Squeezed) {
                    o.runProcess();  // Move the orange to the next state (Processed)
                }

                // Notify Worker 2 that Worker 1 is done
                lock.notify();  // Notify Worker 2 that Worker 1 is done
            } else {
                System.out.println("Worker on " + plant + " has ran out of oranges.");
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
