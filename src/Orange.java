/**
 * @Author Nate Williams
 * @Author John Botonakis
 *
 * This Orange class represents and Orange being processed in the factory. It goes thru
 * stages of fetched, peeled, squeezed, bottled, and finally processed, modeled by an enum
 * as it moves through these states while being processed.
 *
 * Most code here is unchanged, with any comments done by me being stated
 */
public class Orange {
    public enum State {
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

        private static final int finalIndex = State.values().length - 1;

        final int timeToComplete;

        // [JB] The current state of the orange
        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        State getNext() {
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }

    private State state;

    /**
     * // [JB]
     * Constructor for the orange, intializing it in the "Fetched" state.
     * After intialization, it calls doWork to simulate the delay
     */
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    public State getState() {
        return state;
    }

    public void runProcess() {
        // Don't attempt to process an already completed orange
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    private void doWork() {
        // Sleep for the amount of time necessary to do the work
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }
}
