import java.util.concurrent.locks.ReentrantLock;

public class MutexSpin {
    private boolean locked;

    public synchronized boolean acquire() {
        if (locked) {
            return false;
        }
        locked = true;
        return true;
    }

    public synchronized void release() {
        locked = false;
    }

}

