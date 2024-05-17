import java.util.Random;

public class Main {
    public static final int ELEVATORS_COUNT = 4;
    public static final int FLOORS_COUNT = 16;

    public static void main(String[] args) {
        Building building = new Building(ELEVATORS_COUNT, FLOORS_COUNT);
        building.start();
        Thread requestsThread = new Thread(() -> {
            Random generator = new Random();
            while (true) {
                try {
                    Thread.sleep(generator.nextLong(1000, 5000));
                    int from = generator.nextBoolean() ? 0 : generator.nextInt(1, FLOORS_COUNT);
                    boolean up = from == 0 || (generator.nextBoolean() && generator.nextInt(1, FLOORS_COUNT - 1) >= from);
                    building.request(from, up ? Direction.UP : Direction.DOWN);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        requestsThread.start();
    }
}
