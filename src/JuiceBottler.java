public class JuiceBottler {
    public static void main(String[] args) {
        // Create two Plant objects
        Plant plant1 = new Plant(1);  // Pass an identifier for each plant
        Plant plant2 = new Plant(2);  // Pass an identifier for each plant

        System.out.println("Both plants have been created.");
        plant1.startPlant();
        plant2.startPlant();
        System.out.println("Both plants have been started.");


        // After both threads are done, print a simple message
        System.out.println("Both plants have finished processing.");
    }
}
