import java.util.Scanner;

public class Main {
    private static Scanner in;

    public static void main(String[] args) {
        in = new Scanner(System.in);
        System.out.println("""
                Вводите положительные числа или нажимайте Enter для значений по умолчанию
                Для остановки запросов нажмите Enter
                Для завершения симуляции нажмите Enter еще раз
                """);
        int elevators = input("Количество лифтов", 2, 1);
        int floors = input("Количество этажей", 8, 3);
        int simulationTime = input("Задержка между шагами симуляции в мс", 1000, 100);
        int minRequestsTime = input("Минимальная задержка между запросами в мс", 1000, 100);
        int maxRequestsTime = input("Максимальная задержка между запросами в мс", 5000, minRequestsTime);
        Building building = new Building(elevators, floors, simulationTime, minRequestsTime, maxRequestsTime);
        building.start();
        in.nextLine();
        building.stopRequests();
        in.nextLine();
        building.stopSimulation();
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
