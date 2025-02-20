/**
 * <h1>Orange</h1>
 * Represents an orange moving through the juice bottling plant.
 * <p>
 *  {@code Orange} objects are the data units processed by {@link Worker} threads under {@link Plant} control.
 *  Tracks the processing state using the {@link State} enum.
 * </p>
 *
 * <h2>States:</h2>
 * <p>
 *  Transitions through {@link State} enums: {@link State#Fetched}, {@link State#Peeled}, {@link State#Squeezed},
 *  {@link State#Bottled}, {@link State#Processed}. Each state defines processing time.
 * </p>
 *
 * <br> </br>
 * <p>The code here is unchanged. Comments were added after to help me figure out what is going on.</p>
 *
 * @Author Nate Williams
 * @Author John Botonakis
 */
public class Orange {
    //Initalizes a state the orange can be in.
    private State state;

     /**
     * Constructor. Initializes {@code Orange} in {@link State#Fetched} state and simulates initial work.
     */
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    /**
     * Returns the current processing state of this {@code Orange}.
     *
     * @return The current {@link State} of the orange.
     */
    public State getState() {
        return state;
    }

    /**
     * Advances orange to the next processing stage. Simulates work and state transition.
     *
     * @throws IllegalStateException if the orange is already in the {@link State#Processed} state,
     *                               indicating that processing cannot be performed on a completed orange.
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
     * doWork
     * Simulates the work performed on the orange during each processing state.
     * <p>
     * This private method introduces a delay to represent the time taken to complete
     * a specific processing stage (e.g., fetching, peeling, squeezing). The duration of the
     * delay is determined by the {@link State#timeToComplete} value associated with the
     * current {@link State} of the orange.
     * </p>
     * <p>
     * If the thread sleep is interrupted, an error message is printed to the standard error stream,
     * indicating potential issues with orange processing.
     * </p>
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
     * <h1>State Enum</h1>
     * Defines processing stages for {@link Orange}: {@link #Fetched}, {@link #Peeled}, {@link #Squeezed},
     * {@link #Bottled}, {@link #Processed}, each with a processing time.
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
         * Constructor for the {@code State} enum.
         *
         * @param timeToComplete The time in milliseconds required to complete the processing for this state.
         */
        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        /**
         * Gets the next {@code State} in the processing sequence.
         * <p>
         * This method is used to advance an {@link Orange} from its current {@code State} to the subsequent
         * stage in the production line.
         * </p>
         *
         * @return The next {@code State} in the sequence.
         * @throws IllegalStateException if the current state is already {@link #Processed},
         *                               as there are no further states after the final processing stage.
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
