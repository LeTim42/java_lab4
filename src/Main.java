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
                    boolean up = generator.nextBoolean();
                    int floor = generator.nextInt(up ? 0 : 1, FLOORS_COUNT - (up ? 1 : 0));
                    building.request(floor, up ? Direction.UP : Direction.DOWN);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        requestsThread.start();
    }
}
