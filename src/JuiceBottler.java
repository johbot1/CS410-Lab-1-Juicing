import java.util.ArrayList;
import java.util.List;

public class JuiceBottler {
    private static final int NUM_PLANTS = 2;
    public static void main(String[] args) {
        System.out.println("Juice Factory starting up!");

        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i=0; i <NUM_PLANTS; i ++){
            plants[i] = new Plant(i + 1);
            plants[i].startPlant();
        }

        System.out.println("Plants now running!");
        delay(Plant.PROCESSING_TIME, "Juice Factory Malfunction");

        System.out.println("Juice Factory: Time to stop the plants.");
        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }
        System.out.println("Juice Factory: Plants have stopped. Summarizing production.");

        // Summarize the results - Moved summary to JuiceFactory
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles +
                ", wasted " + totalWasted + " oranges");

        System.out.println("Juice Factory shutting down.");
    }

    /**
     * [JB]
     * Utility to pause the program giving some tiny degree of randomness - Moved to JuiceFactory
     *
     * @param time
     * @param errMsg
     */
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }


}



