public class TryFinally {
    public static void main(String[] args) {
        int i = 0;
        try {
            for (int j = 0; j < 5; j++) {
                i++;
            }
            System.out.println("i = " + i);
            i = i / 0;
        } finally {
            i = 2;
            System.out.println("i = " + i);
        }
    }
}
