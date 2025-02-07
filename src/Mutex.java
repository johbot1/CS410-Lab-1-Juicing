import java.util.LinkedList;
import java.util.Queue;

//This ensures mutual exclusion meaning
//only one thread at a time can access a critical section.
public class Mutex {
    //Keeps track of state of locked (true) or unlocked (false)
    private boolean isLocked = false;


    //Synchronized ensures only one thread can modify waitingQueue or isLocked
    //This function will block a thread until it's allowed the Mutex
    public synchronized void acquire() {
        //Gets the current thread, then adds it to the queue
        //to know who is waiting. This ensures first come,
        //first served fairness.
        Thread currentThread = Thread.currentThread();

        //Wait if currently locked, meaning another thread has the mutex
        //or if the current thread is not first in line
        while(isLocked){
            //The thread pauses execution and then releases the lock.
            //If it's interrupted, the thread restores it's interrupted
            //state, ensuring consistency/context preservation
            try{
                wait();
            } catch(InterruptedException e){

            }
        }
        //On exit, the mutex is now free, and the current thread
        //is the first in line in the queue.
        //Remove the current thread from the queue since it don't need
        //to wait anymore, then lock the mutex back up.
        isLocked= true;
    }

    //Unlocks the Mutex, synchronized ensures only one Thread
    //can use the Mutex at a time.
    public synchronized void release() {
        //Unlocks the mutex, waiting for the next thread
        isLocked = false;
        //If there are any threads in the waiting queue,
        //wake them up. I use notifyAll because I don't know
        //which thread specifically will be the next to run
        notifyAll();
    }
}
//Spinlock Mutex
//    private boolean locked;
//
//    public synchronized boolean acquire() {
//        if (locked) {
//            return false;
//        }
//        locked = true;
//        return true;
//    }
//
//    public synchronized void release() {
//        locked = false;
//    }
//}
