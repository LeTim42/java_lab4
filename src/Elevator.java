import java.util.Random;

public class Elevator {
    private final Building building; // ссылка на здание
    private final int id; // номер лифта
    private final int elevators; // количество лифтов в здании
    private final int floors; // количество этажей в здании
    private int currentFloor; // текущий этаж, где находится лифт
    private int currentDirection; // текущее направление, в котором движется лифт
    private int currentPeopleEnteringCount; // текущее количество человек кто хочет зайти в лифт
    private final int[] destinations; // сколько человек собираются выходить на каждом этаже
    private final Random generator;

    public Elevator(Building building, int id) {
        this.building = building;
        this.id = id;
        elevators = building.getElevatorsCount();
        floors = building.getFloorsCount();
        currentFloor = 0;
        currentDirection = Direction.STOP;
        currentPeopleEnteringCount = 0;
        destinations = new int[building.getFloorsCount()];
        generator = new Random();
    }

    public void decide() {
        // остановить лифт, если он движется вниз на первом или вверх на последнем этаже
        if ((currentFloor == 0 && currentDirection == Direction.DOWN) ||
            (currentFloor == floors - 1 && currentDirection == Direction.UP))
            currentDirection = Direction.STOP;

        // если есть заходящие/выходящие люди - ничего не делать
        if (destinations[currentFloor] != 0 || currentPeopleEnteringCount != 0) return;

        // есть ли запросы "по пути" на текущем этаже:
        for (int direction = 0; direction < 2; ++direction) {
            if (currentDirection != 1 - direction) {
                int accepted = building.acceptRequests(currentFloor, direction);
                if (accepted != 0) {
                    currentPeopleEnteringCount = accepted;
                    currentDirection = direction;
                    return;
                }
            }
        }

        boolean[] hasDestinations = new boolean[2]; // есть ли нажатый кнопки этажа снизу/сверху
        boolean[] hasRequests = new boolean[2]; // есть ли запросы снизу/сверху
        int[] nearestRequests = new int[2]; // ближайший запрос снизу/сверху
        for (int above = 0; above < 2; ++above) {
            for (int floor = currentFloor + (above * 2 - 1); floor >= 0 && floor < floors; floor += above * 2 - 1) {
                if (destinations[floor] != 0)
                    hasDestinations[above] = true;
                if (!hasRequests[above] && building.hasRequests(floor)) {
                    hasRequests[above] = true;
                    nearestRequests[above] = floor;
                }
            }
        }

        // если нет ни запросов, ни людей в лифте - остановиться
        if (!hasDestinations[0] && !hasDestinations[1] && !hasRequests[0] && !hasRequests[1]) {
            currentDirection = Direction.STOP;
            return;
        }

        //если нажаты кнопки ниже/выше текущего этажа - двигаться в этом направлении
        for (int direction = 0; direction < 2; ++direction) {
            if (hasDestinations[direction] && currentDirection != 1 - direction) {
                currentDirection = direction;
                return;
            }
        }

        boolean[] hasNearElevator = new boolean[2]; // есть ли лифт, который находится ближе к ближайшему запросу снизу/сверху
        for (int i = 0; i < elevators; ++i) {
            if (i == id) continue;
            int floor = building.getElevatorFloor(i);
            int direction = building.getElevatorDirection(i);
            // самая сложная логика здесь:
            if ((direction != Direction.UP && hasRequests[Direction.DOWN]) &&
                (((floor < currentFloor && floor >= nearestRequests[Direction.DOWN]) || (floor == currentFloor && i < id))))
                    hasNearElevator[Direction.DOWN] = true; // между ближайшим запросом снизу и текущим лифтом есть другой лифт, который не движется вверх
            if ((direction != Direction.DOWN && hasRequests[Direction.UP]) &&
                (((floor > currentFloor && floor <= nearestRequests[Direction.UP]) || (floor == currentFloor && i < id))))
                    hasNearElevator[Direction.UP] = true; // между ближайшим запросом сверху и текущим лифтом есть другой лифт, который не движется вниз
            if ((direction != Direction.DOWN && hasRequests[Direction.DOWN]) &&
                (nearestRequests[Direction.DOWN] >= floor && nearestRequests[Direction.DOWN] - floor < currentFloor - nearestRequests[Direction.DOWN]))
                    hasNearElevator[Direction.DOWN] = true; // ниже ближайшего запроса снизу есть другой лифт, который не едет вниз и находится к запросу ближе, чем текущий лифт
            if ((direction != Direction.UP && hasRequests[Direction.UP]) &&
                (nearestRequests[Direction.UP] <= floor && floor - nearestRequests[Direction.UP] <= nearestRequests[Direction.UP] - currentFloor))
                    hasNearElevator[Direction.UP] = true; // выше ближайшего запроса сверху есть другой лифт, который не едет вверх и находится к запросу не дальше, чем текущий лифт
        }

        // если текущий лифт находится ближе всего к ближайшему запросу снизу/сверху - двигаться в его направлении
        for (int direction = 0; direction < 2; ++direction) {
            if (currentDirection != 1 - direction && hasRequests[direction] && !hasNearElevator[direction]) {
                currentDirection = direction;
                return;
            }
        }

        // если от лифта никому ничего не надо - стоять на месте
        currentDirection = Direction.STOP;
    }

    public void move() {
        if (destinations[currentFloor] != 0) {
            destinations[currentFloor]--;
            log("person went out at floor " + (currentFloor + 1));
        } else if (currentPeopleEnteringCount != 0) {
            int floor = 0;
            if (currentDirection == Direction.UP) {
                floor = generator.nextInt(currentFloor + 1, floors);
            } else if (currentFloor != 1 && generator.nextBoolean()) {
                floor = generator.nextInt(1, currentFloor);
            }
            destinations[floor]++;
            currentPeopleEnteringCount--;
            log("person entered and pushed button " + (floor + 1) + " at floor " + (currentFloor + 1));
        } else if (currentDirection == Direction.UP) {
            currentFloor++;
            log("moves up to floor " + (currentFloor + 1));
        } else if (currentDirection == Direction.DOWN) {
            currentFloor--;
            log("moves down to floor " + (currentFloor + 1));
        }
    }

    public int getFloor() {
        return currentFloor;
    }

    public int getDirection() {
        return currentDirection;
    }

    public boolean hasDestination(int floor) {
        return destinations[floor] != 0;
    }

    private void log(String message) {
        building.log("Elevator " + (id + 1) + ": " + message);
    }
}
