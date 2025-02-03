public class MutexTestSpinLoop implements Runnable {
    private static final int NUM_THREADS = 5;

    public static void main(String[] args) {
        Mutex m = new Mutex();

        for (int i = 0; i < NUM_THREADS; i++) {
            new MutexTestSpinLoop(m, i);
        }
    }

    private final Mutex m;

    MutexTestSpinLoop(Mutex m, int num) {
        this.m = m;
        new Thread(this, "Thread[" + num + "]").start();
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Running");
        try {
            while (!m.acquire())
                continue;
            System.out.println(Thread.currentThread().getName() + " Acquired");
            delay(1000);
        } finally {
            System.out.println(Thread.currentThread().getName() + " Released");
            m.release();
        }
    }

    private void delay(long timeInMs) {
        timeInMs = Math.max(1, timeInMs);
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException ignored) {}
    }
}
