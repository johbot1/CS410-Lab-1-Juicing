import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * //[JB]
 * JuiceBottler
 */
public class JuiceBottler {
    /**
     * getWork
     * This function will check the input list first to assess if it's empty or not.
     * If it is not empty, grab an orange, and remove it from the list.
     * If it IS empty, return null.
     *
     * @param inputList The list of incoming Orange objects
     * @return
     */
    public synchronized static Orange getWork(ConcurrentLinkedQueue<Orange> inputList) {
        if (!inputList.isEmpty()) {
            Orange o = inputList.poll();
            return o;
        } else {
            return null;
        }
    }

    /**
     * sendWork
     * This function will simply add an orange to the export list.
     *
     * @param orange     Orange object to be added
     * @param exportList
     */
    public synchronized static void sendWork(Orange orange, ConcurrentLinkedQueue<Orange> exportList) {
        exportList.add(orange);
    }


}



