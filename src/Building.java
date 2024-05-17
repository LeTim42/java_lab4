import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Building extends Thread {
    private final Elevator[] elevators;
    private final AtomicIntegerArray[] requests;
    // requests[direction][floor] - сколько человек ждут лифт на этаже floor, чтобы поехать в направлении direction
    private final ConcurrentLinkedQueue<String> logs;
    private final int simulationTime;

    public Building(int elevators, int floors, int simulationTime) {
        this.elevators = new Elevator[elevators];
        requests = new AtomicIntegerArray[2];
        logs = new ConcurrentLinkedQueue<>();
        this.simulationTime = simulationTime;
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
                print();
                Thread.sleep(simulationTime);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void request(int floor, int direction) {
        requests[direction].incrementAndGet(floor);
        log("Request " + (direction == Direction.UP ? "up" : "down") + " at floor " + (floor + 1));
    }

    public void log(String message) {
        logs.add(message);
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

    private void print() {
        int floors = getFloorsCount();
        int digits = (int)Math.floor(Math.log10(floors)) + 1;
        while (logs.size() > floors) {
            logs.poll();
        }
        StringBuilder text = new StringBuilder();
        String[] logs = this.logs.toArray(new String[0]);
        log("--------------------------------");
        text.append("\n".repeat(50));
        for (int floor = floors - 1; floor >= 0; --floor) {
            text.append(" ".repeat(digits - (int)Math.floor(Math.log10(floor + 1)))).append(floor + 1).append(' ');
            text.append(requests[0].get(floor) != 0 ? '\u25bc' : ' ').append(requests[1].get(floor) != 0 ? '\u25b2' : ' ').append('\u2502');
            for (Elevator elevator : elevators) {
                if (elevator.getFloor() == floor) {
                    text.append(elevator.getLastAction());
                } else {
                    text.append(elevator.hasDestination(floor) ? '\u25aa' : ' ');
                }
                text.append('\u2502');
            }
            if (floor < logs.length)
                text.append(' ').append(logs[logs.length - floor - 1]);
            text.append('\n');
        }
        System.out.print(text);
    }
}
