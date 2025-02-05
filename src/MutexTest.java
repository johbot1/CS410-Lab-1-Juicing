public class MutexTest implements Runnable {
    private static final int NUM_THREADS = 5;

    public static void main(String[] args) {
        Mutex m = new Mutex();

        for (int i = 0; i < NUM_THREADS; i++) {
            new MutexTest(m, i);
        }
    }

    private final Mutex m;

    MutexTest(Mutex m, int num) {
        this.m = m;
        new Thread(this, "Thread[" + num + "]").start();
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Running");
        m.acquire();
        try {
            System.out.println(Thread.currentThread().getName() + " Acquired");
            delay(1000);
            System.out.println(Thread.currentThread().getName() + " Done");
        } finally {
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
