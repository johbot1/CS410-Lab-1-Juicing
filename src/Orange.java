/**
 * @Author Nate Williams
 * @Author John Botonakis
 * <p>
 * This Orange class represents and Orange being processed in the factory. It goes thru
 * stages of fetched, peeled, squeezed, bottled, and finally processed, modeled by an enum
 * as it moves through these states while being processed.
 * <p>
 * The code here is unchanged. Comments were added after to help me figure out what is going on.
 */
public class Orange {
    //[JB] Initalizes a state the orange can be in.
    private State state;

    /**
     * // [JB]
     * Constructor for the orange, initializing it in the "Fetched" state.
     * After initialization, it calls doWork to simulate the delay
     */
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    /**
     * //[JB]
     * Standard getter for state.
     *
     * @return Oranges current state
     */
    public State getState() {
        return state;
    }

    /**
     * //[JB]
     * runProcess
     * If the state is processed, it cannot progress any further.
     * As long as it's not "Processed", call doWork. After the work is done,
     * progress to the next state.
     */
    public void runProcess() {
        // Don't attempt to process an already completed orange
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    /**
     * //[JB]
     * doWork
     * The "work" being done is simply sleeping the thread for the amount of time
     * specified in the possible states enum. Any error will be recorded.
     */
    private void doWork() {
        // Sleep for the amount of time necessary to do the work
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }

    /**
     * //[JB]
     * State enum
     * All possible states for an orange, with various times to complete work
     * when calling "doWork".
     */
    public enum State {
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

        private static final int finalIndex = State.values().length - 1;

        final int timeToComplete;

        /**
         * //[JB]
         * Just returns a completion time for the specified state.
         *
         * @param timeToComplete
         */
        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        /**
         * //[JB]
         * getNext
         * Compares the current index of the Orange state with the last state.
         * If it's not already at it's last state, progress to the next state.
         *
         * @return
         */
        State getNext() {
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }
}
