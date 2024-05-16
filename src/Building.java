import java.util.concurrent.atomic.AtomicIntegerArray;

public class Building extends Thread {
    private final Elevator[] elevators;
    private final AtomicIntegerArray[] requests;
    // requests[direction][floor] - сколько человек ждут лифт на этаже floor, чтобы поехать в направлении direction

    public Building(int elevators, int floors) {
        this.elevators = new Elevator[elevators];
        requests = new AtomicIntegerArray[2];
        requests[0] = new AtomicIntegerArray(floors);
        requests[1] = new AtomicIntegerArray(floors);
        for (int i = 0; i < elevators; ++i) {
            this.elevators[i] = new Elevator(this, i);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Elevator elevator : elevators) {
                    elevator.decide();
                }
                for (Elevator elevator : elevators) {
                    elevator.move();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void request(int floor, int direction) {
        requests[direction].incrementAndGet(floor);
        System.out.println("Request " + (direction == Direction.UP ? "up" : "down") + " at floor " + (floor + 1));
    }

    public int getElevatorsCount() {
        return elevators.length;
    }

    public int getFloorsCount() {
        return requests[0].length();
    }

    public int acceptRequests(int floor, int direction) {
        return requests[direction].getAndSet(floor, 0);
    }

    public boolean hasRequests(int floor) {
        return requests[0].get(floor) != 0 || requests[1].get(floor) != 0;
    }

    public int getElevatorFloor(int id) {
        return elevators[id].getFloor();
    }

    public int getElevatorDirection(int id) {
        return elevators[id].getDirection();
    }
}
