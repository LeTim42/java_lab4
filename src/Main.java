import java.util.Random;
import java.util.Scanner;

public class Main {
    private static Scanner in;

    public static void main(String[] args) {
        in = new Scanner(System.in);
        System.out.println("Вводите положительные числа или просто нажимайте Enter для значений по умолчанию");
        int elevators = input("Количество лифтов", 2, 1);
        int floors = input("Количество этажей", 5, 3);
        int minTime = input("Минимальное время между запросами в мс", 1000, 100);
        int maxTime = input("Максимальное время между запросами в мс", 5000, minTime);
        int simulationTime = input("Скорость лифтов в мс", 1000, 100);
        Building building = new Building(elevators, floors, simulationTime);
        building.start();
        Thread requestsThread = new Thread(() -> {
            Random generator = new Random();
            while (true) {
                try {
                    Thread.sleep(generator.nextLong(minTime, maxTime + 1));
                    int floor = generator.nextBoolean() ? 0 : generator.nextInt(1, floors);
                    boolean up = floor == 0 || (generator.nextBoolean() && generator.nextInt(1, floors - 1) >= floor);
                    building.request(floor, up ? Direction.UP : Direction.DOWN);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        requestsThread.start();
    }

    private static int input(String message, int def, int min) {
        while (true) {
            System.out.print(message + " (по умолчанию " + def + "): ");
            String line = in.nextLine();
            if (line.isEmpty()) return def;
            int num;
            try {
                num = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Введите число не менее " + min);
                continue;
            }
            if (num >= min) return num;
            System.out.println("Введите число не менее " + min);
        }
    }
}
