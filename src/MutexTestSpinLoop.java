public class MutexTestSpinLoop implements Runnable {
    //TL;DR: The main thread starts 5 new threads (T[0],T[1],...T[4]
    //Each thread prints running, but doesn't necessarily execute in order
    //Only one thread can enter the 'acquire' section at a time
    //Thread prints "Acquired", waits 1 second, then releases the mutex
    //The next thread in the queue wakes up and repeats the process.


    //Initializes 5 threads
    private static final int NUM_THREADS = 5;

    //Creates a mutex instance every thread will share,
    //Then creates 5 child threads, each running a SpinLoop instance.
    //Order of execution is NOT gauranteed but all threads will
    //eventually try to get time with the Mutex.
    public static void main(String[] args) {
        Mutex m = new Mutex();

        for (int i = 0; i < NUM_THREADS; i++) {
            new MutexTestSpinLoop(m, i);
        }
    }

    //Stores the mutex so everythread uses the same lock.
    private final Mutex m;
    MutexTestSpinLoop(Mutex m, int num) {
        this.m = m;
        //Creates then immidiately starts a new thread
        //Threads will not necessarily start in order
        //This is due to the OS scheduling
        new Thread(this, "Thread[" + num + "]").start();
    }

    //First lets the user know the app is running
    //The thread will then try to acquire the mutex.
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Running");
        //Then, lets the user know it's acquired.
        //Thread will delay or hold onto the mutex for 1 second before release.
        try {
            System.out.println(Thread.currentThread().getName() + " Acquired");
            delay(1000);
        }//Ensures teh mutex is always released, regardless of exception
        //This will allow the next waiting thread to proceed.
        finally {
            System.out.println(Thread.currentThread().getName() + " Released");
            m.release();
        }
    }

    private void delay(long timeInMs) {
        //Ensures the time is at least a millisecond to not instantly return
        timeInMs = Math.max(1, timeInMs);
        //Pauses for execution time
        //If interrupted, ignore the exception
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException ignored) {}
    }

}
