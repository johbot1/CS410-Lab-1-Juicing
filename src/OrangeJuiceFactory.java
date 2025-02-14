/**
 * @Author John Botonakis
 *
 * Serves as the main controller for the simulation. It is responsible for intializing plants + workers,
 * running the sim, waiting for shutdown, and summarizing the results.
 */
import java.util.ArrayList;
import java.util.List;

public class OrangeJuiceFactory {
    private static final int NUM_WORKERS_PER_PLANT = 2;
    private static final long PROCESSING_TIME = Plant.PROCESSING_TIME;

    public static void main(String[] args) {
        // Create and start plants
        Plant[] plants = new Plant[2];
        List<Thread> workerThreads = new ArrayList<>();

        for (int i = 0; i < plants.length; i++) {
            plants[i] = new Plant(i + 1);
            plants[i].startPlant();
        }

        // Create workers and assign them to plants
        for (Plant plant : plants) {
            for (int j = 0; j < NUM_WORKERS_PER_PLANT; j++) {
                Worker worker = new Worker(plant, plant.getOrangeQueue());
                Thread workerThread = new Thread(worker, "Worker-" + (j + 1) + "-Plant-" + plant);
                workerThreads.add(workerThread);
                workerThread.start();
            }
        }

        // Let the simulation run for a fixed duration
        try {
            Thread.sleep(PROCESSING_TIME);
        } catch (InterruptedException e) {
            System.err.println("Factory encountered an issue.");
        }

        // Stop the plants
        for (Plant plant : plants) {
            plant.stopPlant();
        }

        // Notify workers to stop by placing `null` into each queue
        for (Plant plant : plants) {
            for (int j = 0; j < NUM_WORKERS_PER_PLANT; j++) {
                plant.getOrangeQueue().enqueue(null);
            }
        }

        // Wait for all worker threads to finish
        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                System.err.println(workerThread.getName() + " did not terminate properly.");
            }
        }

        // Wait for plants to shut down
        for (Plant plant : plants) {
            plant.waitToStop();
        }

        // Summarize results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant plant : plants) {
            totalProvided += plant.getProvidedOranges();
            totalProcessed += plant.getProcessedOranges();
            totalBottles += plant.getBottles();
            totalWasted += plant.getWaste();
        }

        System.out.println("DEBUG: Provided = " + totalProvided + ", Processed = " + totalProcessed + ", Bottles = " + totalBottles + ", Wasted = " + totalWasted);
        System.out.println("Expected Max Processed: " + (totalProvided - totalWasted));

        System.out.println("Final Report:");
        System.out.println("Total Oranges Provided: " + totalProvided);
        System.out.println("Total Oranges Processed: " + totalProcessed);
        System.out.println("Bottles Created: " + totalBottles);
        System.out.println("Wasted Oranges: " + totalWasted);
        System.out.println("Simulation complete.");
    }
}
